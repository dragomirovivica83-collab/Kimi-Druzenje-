package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.User
import com.example.data.model.Message
import com.example.data.model.Friend
import com.example.data.model.Punishment
import com.example.data.repository.KimiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class KimiViewModel(private val repository: KimiRepository) : ViewModel() {

    // --- Authentication State ---
    private val _prijavljeniKorisnik = MutableStateFlow<User?>(null)
    val prijavljeniKorisnik: StateFlow<User?> = _prijavljeniKorisnik.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // --- App Lists ---
    val sviKorisnici: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sveKazne: StateFlow<List<Punishment>> = repository.allPunishments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Active Chat System ---
    private val _aktivniCaskalacId = MutableStateFlow<Int?>(null)
    val aktivniCaskalacId: StateFlow<Int?> = _aktivniCaskalacId.asStateFlow()

    val aktivnePoruke: StateFlow<List<Message>> = _aktivniCaskalacId
        .flatMapLatest { partnerId ->
            val loggedInId = _prijavljeniKorisnik.value?.id ?: 0
            if (partnerId != null && loggedInId != 0) {
                repository.getMessagesBetweenUsers(loggedInId, partnerId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Friends and Requests List ---
    val prijateljstva: StateFlow<List<Friend>> = _prijavljeniKorisnik
        .flatMapLatest { user ->
            if (user != null) {
                repository.getFriendsAndRequests(user.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Login & Registration ---
    fun prijaviSe(korisnickoIme: String, lozinka: String, onSuccess: () -> Unit) {
        _authError.value = null
        viewModelScope.launch {
            val user = repository.getUserByUsername(korisnickoIme)
            if (user == null) {
                _authError.value = "Korisničko ime ne postoji!"
            } else if (user.status == "Banovan") {
                _authError.value = "Vaš nalog je trajno banovan sa platforme zbog kršenja pravila!"
            } else if (user.lozinka != lozinka) {
                _authError.value = "Pogrešna lozinka!"
            } else {
                _prijavljeniKorisnik.value = user
                onSuccess()
            }
        }
    }

    fun registrujSe(
        korisnickoIme: String,
        lozinka: String,
        godine: Int,
        grad: String,
        pol: String,
        biografija: String,
        onSuccess: () -> Unit
    ) {
        _authError.value = null
        if (korisnickoIme.length < 3) {
            _authError.value = "Korisničko ime mora imati s najmanje 3 karaktera!"
            return
        }
        if (lozinka.length < 3) {
            _authError.value = "Lozinka mora imati najmanje 3 karaktera!"
            return
        }
        if (godine < 16) {
            _authError.value = "Morate imati najmanje 16 godina za korišćenje aplikacije!"
            return
        }

        viewModelScope.launch {
            val postojeci = repository.getUserByUsername(korisnickoIme)
            if (postojeci != null) {
                _authError.value = "Korisničko ime '$korisnickoIme' je već zauzeto!"
            } else {
                val noviUser = User(
                    username = korisnickoIme,
                    lozinka = lozinka,
                    uloga = "Korisnik", // Default role
                    status = "Aktivni",
                    avatarUrl = "preset_default",
                    godine = godine,
                    grad = grad,
                    biografija = biografija,
                    pol = pol
                )
                val generisaniId = repository.registerUser(noviUser)
                val registrovani = noviUser.copy(id = generisaniId.toInt())
                _prijavljeniKorisnik.value = registrovani
                onSuccess()
            }
        }
    }

    fun odjaviSe() {
        _prijavljeniKorisnik.value = null
        _aktivniCaskalacId.value = null
    }

    // --- Chat Operations ---
    fun postaviAktivnogCaskalca(partnerId: Int?) {
        _aktivniCaskalacId.value = partnerId
        val loggedInId = _prijavljeniKorisnik.value?.id ?: 0
        if (partnerId != null && loggedInId != 0) {
            viewModelScope.launch {
                repository.markMessagesAsRead(partnerId, loggedInId)
            }
        }
    }

    fun posaljiPoruku(tekst: String) {
        val loggedInUser = _prijavljeniKorisnik.value ?: return
        val partnerId = _aktivniCaskalacId.value ?: return

        if (loggedInUser.status == "Utišan") {
            _authError.value = "Trenutno ste utišani od strane administracije i ne možete slati poruke!"
            return
        }

        if (tekst.isBlank()) return

        viewModelScope.launch {
            val novaPoruka = Message(
                posiljalacId = loggedInUser.id,
                primalacId = partnerId,
                tekst = tekst,
                procitano = false
            )
            repository.sendMessage(novaPoruka)
        }
    }

    // --- Swipe & Match Candidates ---
    // Returns users that are not the currently logged in user, and not Kimi AI
    fun getSwipeKandidati(): Flow<List<User>> {
        return combine(sviKorisnici, prijavljeniKorisnik) { list, currentUser ->
            if (currentUser == null) {
                emptyList()
            } else {
                list.filter { it.id != currentUser.id && it.id != 999 && it.status != "Banovan" }
            }
        }
    }

    // Swiping / Likes are implemented seamlessly via the Friendship system.
    // Swipe Right (Like) -> creates a FriendRequest or completes a friendship!
    fun prevuciDesnoSvidjaMiSe(targetUser: User, onMatch: () -> Unit) {
        val loggedInUser = _prijavljeniKorisnik.value ?: return
        viewModelScope.launch {
            // Check if there is an existing relation
            val postojecaRelacija = repository.getFriendRelation(loggedInUser.id, targetUser.id)
            if (postojecaRelacija == null) {
                // Sent request
                repository.sendFriendRequest(
                    Friend(
                        posiljalacId = loggedInUser.id,
                        primalacId = targetUser.id,
                        status = "Zahtev"
                    )
                )
            } else if (postojecaRelacija.posiljalacId == targetUser.id && postojecaRelacija.status == "Zahtev") {
                // Target has already liked current user! MATCH!
                repository.acceptFriendRequest(postojecaRelacija.id)
                
                // Add mutual greetings automatically to open the chat!
                repository.sendMessage(
                    Message(
                        posiljalacId = targetUser.id,
                        primalacId = loggedInUser.id,
                        tekst = "Uparili smo se! 🎉 Drago mi je da smo se spojili na Kimi Druženju.",
                        procitano = false
                    )
                )
                onMatch()
            }
        }
    }

    // --- Friend System ---
    fun dodajPrijateljaPoImenu(username: String, result: (String) -> Unit) {
        val loggedInUser = _prijavljeniKorisnik.value ?: return
        if (username.lowercase() == loggedInUser.username.lowercase()) {
            result("Ne možete dodati sami sebe!")
            return
        }

        viewModelScope.launch {
            val target = repository.getUserByUsername(username)
            if (target == null) {
                result("Korisnik sa tim korisničkim imenom nije pronađen!")
            } else if (target.id == 999) {
                result("Kimi AI je tvoj večiti asistent, već je dostupan u četu!")
            } else {
                val postojeca = repository.getFriendRelation(loggedInUser.id, target.id)
                if (postojeca == null) {
                    repository.sendFriendRequest(
                        Friend(
                            posiljalacId = loggedInUser.id,
                            primalacId = target.id,
                            status = "Zahtev"
                        )
                    )
                    result("Zahtev za prijateljstvo poslat korisniku $username!")
                } else if (postojeca.status == "Prijatelji") {
                    result("Već ste prijatelji sa ovim korisnikom!")
                } else if (postojeca.posiljalacId == target.id) {
                    // Accept request
                    repository.acceptFriendRequest(postojeca.id)
                    result("Prihvatili ste zahtev za prijateljstvo od korisnika $username!")
                } else {
                    result("Već ste poslali zahtev ovom korisniku!")
                }
            }
        }
    }

    fun prihvatiPrijatelja(friendshipId: Int) {
        viewModelScope.launch {
            repository.acceptFriendRequest(friendshipId)
        }
    }

    fun odbijIliObrisiPrijatelja(friendship: Friend) {
        viewModelScope.launch {
            repository.removeFriendOrRequest(friendship)
        }
    }

    // --- User Profile Editing ---
    fun azurirajProfil(novoIme: String, godine: Int, grad: String, pol: String, biografija: String, avatarPreset: String) {
        val loggedInUser = _prijavljeniKorisnik.value ?: return
        viewModelScope.launch {
            val updated = loggedInUser.copy(
                username = novoIme,
                godine = godine,
                grad = grad,
                pol = pol,
                biografija = biografija,
                avatarUrl = avatarPreset
            )
            repository.updateUser(updated)
            _prijavljeniKorisnik.value = updated
        }
    }

    // --- Administration and Moderation Panel ---
    // Rules: Vlasnik has ultimate access, can promote / demote, can punish.
    // Admin can promote to Moderator/Helper, punish Korisnik.
    // Moderator can warn and mute.
    // Helper can warn.
    fun kazniKorisnika(targetUser: User, tipKazne: String, razlog: String, modUser: User) {
        viewModelScope.launch {
            val punishment = Punishment(
                targetUserId = targetUser.id,
                targetUsername = targetUser.username,
                moderatorId = modUser.id,
                moderatorUsername = modUser.username,
                tipKazne = tipKazne,
                razlog = razlog
            )
            repository.logPunishment(punishment)
        }
    }

    fun promeniUloguKorisnika(targetUserId: Int, novaUloga: String) {
        viewModelScope.launch {
            repository.updateUserRole(targetUserId, novaUloga)
        }
    }

    fun obrisiNalog(targetUserId: Int) {
        viewModelScope.launch {
            repository.deleteUser(targetUserId)
        }
    }
}

class KimiViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KimiViewModel::class.java)) {
            val repo = KimiRepository(context)
            @Suppress("UNCHECKED_CAST")
            return KimiViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
