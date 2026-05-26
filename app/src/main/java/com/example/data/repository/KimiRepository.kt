package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.api.GeminiClient
import com.example.data.local.AppDatabase
import com.example.data.model.User
import com.example.data.model.Message
import com.example.data.model.Friend
import com.example.data.model.Punishment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KimiRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val messageDao = db.messageDao()
    private val friendDao = db.friendDao()
    private val punishmentDao = db.punishmentDao()

    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val allPunishments: Flow<List<Punishment>> = punishmentDao.getAllPunishments()

    init {
        // Pre-populate the database if empty
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existing = userDao.getAllUsers().firstOrNull()
                if (existing.isNullOrEmpty()) {
                    prePopulateDatabase()
                }
            } catch (e: Exception) {
                Log.e("KimiRepository", "Error checking/populating database", e)
            }
        }
    }

    // --- User Operations ---
    fun getUserById(id: Int): Flow<User?> = userDao.getUserById(id)
    suspend fun getUserByIdSync(id: Int): User? = userDao.getUserByIdSync(id)
    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)
    suspend fun registerUser(user: User): Long = userDao.registerUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun updateUserRole(userId: Int, role: String) = userDao.updateUserRole(userId, role)
    suspend fun updateUserStatus(userId: Int, status: String) = userDao.updateUserStatus(userId, status)
    suspend fun deleteUser(userId: Int) = userDao.deleteUser(userId)

    // --- Message Operations ---
    fun getMessagesBetweenUsers(userOneId: Int, userTwoId: Int): Flow<List<Message>> =
        messageDao.getMessagesBetweenUsers(userOneId, userTwoId)

    suspend fun sendMessage(message: Message) {
        messageDao.sendMessage(message)
        
        // Custom Kimi AI feature: if message receiver is Kimi AI (id = 999)
        if (message.primalacId == 999) {
            triggerKimiAIResponse(message.posiljalacId, message.tekst)
        }
    }

    suspend fun markMessagesAsRead(senderId: Int, receiverId: Int) =
        messageDao.markMessagesAsRead(senderId, receiverId)

    // --- Friend Operations ---
    fun getFriendsAndRequests(userId: Int): Flow<List<Friend>> = friendDao.getFriendsAndRequests(userId)
    suspend fun getFriendRelation(userOneId: Int, userTwoId: Int): Friend? = friendDao.getFriendRelation(userOneId, userTwoId)
    suspend fun sendFriendRequest(friend: Friend) = friendDao.sendFriendRequest(friend)
    suspend fun acceptFriendRequest(id: Int) = friendDao.acceptFriendRequest(id)
    suspend fun removeFriendOrRequest(friend: Friend) = friendDao.removeFriendOrRequest(friend)

    // --- Punishment Operations ---
    suspend fun logPunishment(punishment: Punishment) {
        punishmentDao.logPunishment(punishment)
        // Also update user status in the users database table accordingly!
        val noviStatus = when (punishment.tipKazne) {
            "Utišaj" -> "Utišan"
            "Banuj" -> "Banovan"
            "Odblokiraj" -> "Aktivni"
            else -> "Aktivni"
        }
        userDao.updateUserStatus(punishment.targetUserId, noviStatus)
    }

    // --- AI Chat Logic ---
    private suspend fun triggerKimiAIResponse(korisnikId: Int, korisnikPoruka: String) {
        withContext(Dispatchers.IO) {
            val systemInstructions = """
                Vi ste Kimi AI, zvanični virtuelni asistent u aplikaciji "Kimi Druženje".
                Aplikacija je namenjena za dopisivanje (chat), upoznavanje (dating/match) i ima modernu plavu pozadinu.
                Takođe, aplikacija ima Admin panel sa ulogama: Vlasnik, Admin, Moderator, Helper.
                Pravila zajednice su stroga protiv vređanja, lažnih naloga i spama.
                Odgovarajte holds, prijateljski, toplo, na SRPSKOM JEZIKU.
                Ohrabrujte korisnike da se druže, šalju zahteve za prijateljstvo i budu aktivni na platformi.
                Držite odgovore relativno kratkim i u stilu čet poruka (Messenger).
            """.trimIndent()
            
            val odgovor = GeminiClient.generisiOdgovor(systemInstructions, korisnikPoruka)
            
            val aiMessage = Message(
                posiljalacId = 999, // Kimi AI id is 999
                primalacId = korisnikId,
                tekst = odgovor,
                procitano = false
            )
            messageDao.sendMessage(aiMessage)
        }
    }

    // --- Dummy Data Prep ---
    private suspend fun prePopulateDatabase() {
        // Special System User: Kimi AI
        val aiUser = User(
            id = 999,
            username = "Kimi AI 🤖",
            lozinka = "kimi_ai_secret_pass_123",
            uloga = "Helper",
            status = "Aktivni",
            avatarUrl = "preset_0",
            godine = 100,
            grad = "Zvezdano nebo",
            biografija = "Ja sam vaš asistent i virtuelni saputnik u Kimi Druženje aplikaciji! 🚀 Pitajte me bilo šta o četovanju, upoznavanju ili ulogama.",
            pol = "Ostalo"
        )
        userDao.registerUser(aiUser)

        // Standard test roles
        userDao.registerUser(User(
            username = "vlasnik",
            lozinka = "123",
            uloga = "Vlasnik",
            status = "Aktivni",
            avatarUrl = "preset_owner",
            godine = 30,
            grad = "Novi Sad",
            biografija = "Glavni programer i osnivač Kimi Druženje sistema.",
            pol = "Muško"
        ))

        userDao.registerUser(User(
            username = "admin",
            lozinka = "123",
            uloga = "Admin",
            status = "Aktivni",
            avatarUrl = "preset_admin",
            godine = 28,
            grad = "Beograd",
            biografija = "Glavni administrator. Obratite mi se ako uočite kršenje kućnog reda.",
            pol = "Muško"
        ))

        userDao.registerUser(User(
            username = "moderator",
            lozinka = "123",
            uloga = "Moderator",
            status = "Aktivni",
            avatarUrl = "preset_mod",
            godine = 26,
            grad = "Niš",
            biografija = "Zadovoljstvo mi je da održavam red i mir. Budite pristojni u četu! 😊",
            pol = "Žensko"
        ))
        
        userDao.registerUser(User(
            username = "helper",
            lozinka = "123",
            uloga = "Helper",
            status = "Aktivni",
            avatarUrl = "preset_helper",
            godine = 22,
            grad = "Subotica",
            biografija = "Tu sam da pomognem novim članovima da se snađu. Pišite slobodno!",
            pol = "Muško"
        ))

        // Prepopulate gorgeous dating profiles
        val profiles = listOf(
            User(
                username = "milica", lozinka = "123", uloga = "Korisnik", status = "Aktivni", avatarUrl = "preset_1",
                godine = 23, grad = "Beograd", biografija = "Ljubitelj kafe ☕, fotografije i šetnje pored reke. Tražim iskrenog dečka za druženje i možda nešto više! 🥀", pol = "Žensko"
            ),
            User(
                username = "jelena", lozinka = "123", uloga = "Korisnik", status = "Aktivni", avatarUrl = "preset_2",
                godine = 21, grad = "Novi Sad", biografija = "Uvek pozitivna! ✨ Obožavam ples, izlaske i dobru muziku. Ajmo na kafu?", pol = "Žensko"
            ),
            User(
                username = "nikola", lozinka = "123", uloga = "Korisnik", status = "Aktivni", avatarUrl = "preset_3",
                godine = 25, grad = "Niš", biografija = "Aktivno treniram fudbal, volim putovanja i planinske ture. Tražim simpatičnu devojku sa dobrom energijom!", pol = "Muško"
            ),
            User(
                username = "stefan", lozinka = "123", uloga = "Korisnik", status = "Aktivni", avatarUrl = "preset_4",
                godine = 24, grad = "Kragujevac", biografija = "Programer po danu, gitarista po noći 🎸. Obožavam rok muziku, pivo i noćno planinarenje. Piši mi!", pol = "Muško"
            ),
            User(
                username = "marija", lozinka = "123", uloga = "Korisnik", status = "Aktivni", avatarUrl = "preset_5",
                godine = 22, grad = "Subotica", biografija = "Ljubitelj životinja 🐾, čokolade i putovanja. Volim duge razgovore pod zvezdanim nebom 🌌", pol = "Žensko"
            ),
            User(
                username = "sandra_bg", lozinka = "123", uloga = "Korisnik", status = "Aktivni", avatarUrl = "preset_6",
                godine = 26, grad = "Pančevo", biografija = "Iskrena i direktna. Cenim kulturno ponašanje i smisao za humor. Volim bioskop i šetnje.", pol = "Žensko"
            )
        )

        for (profile in profiles) {
            userDao.registerUser(profile)
        }

        // Add some pre-loaded dialogue history with Kimi AI so that users have a chat active
        val welcomeMsg = Message(
            posiljalacId = 999,
            primalacId = 2, // 'vlasnik' which is registered first with ID 2
            tekst = "Dobrodošao u Kimi Druženje! 🎉 Ja sam Kimi AI, tvoj inteligentni asistent. Možeš mi pisati o svemu, a takođe možeš lajkovati i upoznavati druge članove. Jesi li spreman za nova poznanstva?",
            procitano = false
        )
        messageDao.sendMessage(welcomeMsg)
    }
}
