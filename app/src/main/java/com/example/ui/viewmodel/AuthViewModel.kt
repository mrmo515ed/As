package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.User
import com.example.data.repository.AnimeRepository
import com.example.data.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: User, val message: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
    data class PasswordResetSent(val message: String) : AuthUiState
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<User?> = animeRepository.currentUser

    val isUserLoggedIn: Boolean
        get() = authRepository.getCurrentFirebaseUser() != null || currentUser.value != null

    fun login(
        email: String,
        pass: String,
        onSuccess: (User) -> Unit = {}
    ) {
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()

        if (trimmedEmail.isBlank()) {
            _uiState.value = AuthUiState.Error("يرجى إدخال البريد الإلكتروني")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _uiState.value = AuthUiState.Error("صيغة البريد الإلكتروني غير صالحة")
            return
        }

        if (trimmedPass.isBlank()) {
            _uiState.value = AuthUiState.Error("يرجى إدخال كلمة المرور")
            return
        }

        if (trimmedPass.length < 6) {
            _uiState.value = AuthUiState.Error("كلمة المرور يجب ألا تقل عن 6 أحرف")
            return
        }

        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = authRepository.loginWithEmail(trimmedEmail, trimmedPass)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user, "أهلاً بك مجدداً يا ${user.name}!")
                onSuccess(user)
            }.onFailure { exception ->
                val errorMsg = mapAuthException(exception)
                _uiState.value = AuthUiState.Error(errorMsg)
            }
        }
    }

    fun register(
        name: String,
        email: String,
        pass: String,
        confirmPass: String,
        onSuccess: (User) -> Unit = {}
    ) {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPass = pass.trim()
        val trimmedConfirm = confirmPass.trim()

        if (trimmedName.isBlank()) {
            _uiState.value = AuthUiState.Error("يرجى إدخال اسم الأوتاكو أو الاسم المستعار")
            return
        }

        if (trimmedEmail.isBlank()) {
            _uiState.value = AuthUiState.Error("يرجى إدخال البريد الإلكتروني")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _uiState.value = AuthUiState.Error("صيغة البريد الإلكتروني غير صالحة")
            return
        }

        if (trimmedPass.isBlank()) {
            _uiState.value = AuthUiState.Error("يرجى إدخال كلمة المرور")
            return
        }

        if (trimmedPass.length < 6) {
            _uiState.value = AuthUiState.Error("كلمة المرور يجب أن تكون 6 أحرف على الأقل")
            return
        }

        if (trimmedPass != trimmedConfirm) {
            _uiState.value = AuthUiState.Error("كلمتا المرور غير متطابقتين")
            return
        }

        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = authRepository.registerWithEmail(trimmedName, trimmedEmail, trimmedPass)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user, "مرحباً بك في أنمي بلاك، ${user.name}!")
                onSuccess(user)
            }.onFailure { exception ->
                val errorMsg = mapAuthException(exception)
                _uiState.value = AuthUiState.Error(errorMsg)
            }
        }
    }

    fun resetPassword(email: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _uiState.value = AuthUiState.Error("يرجى إدخال بريد إلكتروني صحيح لإعادة التعيين")
            return
        }

        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(trimmedEmail)
            result.onSuccess {
                _uiState.value = AuthUiState.PasswordResetSent("تم إرسال رابط استعادة كلمة المرور إلى بريدك الإلكتروني بنجاح.")
            }.onFailure { exception ->
                val errorMsg = mapAuthException(exception)
                _uiState.value = AuthUiState.Error(errorMsg)
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState.Idle
    }

    fun clearState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun mapAuthException(e: Throwable): String {
        return when (e) {
            is FirebaseAuthInvalidUserException -> "لا يوجد حساب مسجل بهذا البريد الإلكتروني."
            is FirebaseAuthInvalidCredentialsException -> "البريد الإلكتروني أو كلمة المرور غير صحيحة."
            is FirebaseAuthUserCollisionException -> "هذا البريد الإلكتروني مسجل بالفعل بحساب آخر."
            is FirebaseAuthWeakPasswordException -> "كلمة المرور ضعيفة جداً. يرجى اختيار كلمة مرور أقوى."
            is FirebaseNetworkException -> "تعذر الاتصال بالخادم. يرجى التحقق من اتصال الإنترنت والمحاولة ثانية."
            else -> {
                val msg = e.message ?: ""
                when {
                    msg.contains("network", ignoreCase = true) -> "تعذر الاتصال بالإنترنت، يرجى التحقق من الشبكة."
                    msg.contains("password", ignoreCase = true) -> "كلمة المرور غير صحيحة أو ضعيفة."
                    msg.contains("user-not-found", ignoreCase = true) -> "لا يوجد حساب بهذا البريد الإلكتروني."
                    msg.contains("email-already-in-use", ignoreCase = true) -> "البريد الإلكتروني مستخدم مسبقاً."
                    else -> "حدث خطأ أثناء العملية: ${e.localizedMessage ?: "يرجى المحاولة لاحقاً"}"
                }
            }
        }
    }
}

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val animeRepository: AnimeRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository, animeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
