package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

/**
 * Full-screen Authentication Hub containing Login & Register views powered by AuthViewModel.
 */
@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    initialRegisterMode: Boolean = false,
    onAuthSuccess: () -> Unit = {},
    onContinueAsGuest: () -> Unit = {}
) {
    var isRegisterMode by remember { mutableStateOf(initialRegisterMode) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .testTag("auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Logo & Branding
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(BgCardElevated)
                    .border(1.5.dp, AnimeGradient, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "أنمي بلاك",
                    tint = NeonCyan,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "أنمي بلاك",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = if (isRegisterMode) "أنشئ حسابك وانضم لنقابات الأوتاكو" else "مرحباً بك مجدداً في عالم الأنمي الأسطوري",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Selector (تسجيل الدخول / إنشاء حساب)
            Surface(
                color = BgSurfaceDark,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    val loginSelected = !isRegisterMode
                    Button(
                        onClick = {
                            viewModel.clearState()
                            isRegisterMode = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (loginSelected) NeonCyan else Color.Transparent,
                            contentColor = if (loginSelected) BgDeepVoid else TextMuted
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_login")
                    ) {
                        Text(
                            text = "تسجيل الدخول",
                            fontWeight = if (loginSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.clearState()
                            isRegisterMode = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRegisterMode) NeonCyan else Color.Transparent,
                            contentColor = if (isRegisterMode) BgDeepVoid else TextMuted
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_register")
                    ) {
                        Text(
                            text = "إنشاء حساب",
                            fontWeight = if (isRegisterMode) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // UI State Banner (Error or Success or Reset sent)
                    AnimatedVisibility(
                        visible = uiState is AuthUiState.Error,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val error = (uiState as? AuthUiState.Error)?.message ?: ""
                        Surface(
                            color = CrimsonRed.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CrimsonRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(bottom = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = CrimsonRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = error,
                                    color = CrimsonRed,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearState() },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "إغلاق",
                                        tint = CrimsonRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState is AuthUiState.PasswordResetSent,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val msg = (uiState as? AuthUiState.PasswordResetSent)?.message ?: ""
                        Surface(
                            color = EmeraldGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, EmeraldGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(bottom = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = msg,
                                    color = EmeraldGreen,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    if (isRegisterMode) {
                        RegisterForm(
                            viewModel = viewModel,
                            isLoading = uiState is AuthUiState.Loading,
                            onSwitchToLogin = {
                                viewModel.clearState()
                                isRegisterMode = false
                            }
                        )
                    } else {
                        LoginForm(
                            viewModel = viewModel,
                            isLoading = uiState is AuthUiState.Loading,
                            onSwitchToRegister = {
                                viewModel.clearState()
                                isRegisterMode = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Guest access button
            OutlinedButton(
                onClick = onContinueAsGuest,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("guest_login_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "المتابعة والتصفح كزائر",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LoginForm(
    viewModel: AuthViewModel,
    isLoading: Boolean,
    onSwitchToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Email Field
        Text(text = "البريد الإلكتروني", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("example@domain.com", color = TextMuted, fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = NeonCyan) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle,
                focusedContainerColor = BgCardElevated,
                unfocusedContainerColor = BgCardElevated
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_email_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Password Field
        Text(text = "كلمة المرور", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("••••••••", color = TextMuted, fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonCyan) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور",
                        tint = TextSecondary
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle,
                focusedContainerColor = BgCardElevated,
                unfocusedContainerColor = BgCardElevated
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot password button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "نسيت كلمة المرور؟",
                color = NeonPurple,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { showResetDialog = true }
                    .padding(vertical = 4.dp)
                    .testTag("forgot_password_btn")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                viewModel.login(email, password)
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan,
                disabledContainerColor = NeonCyan.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("login_submit_btn")
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = BgDeepVoid,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "تسجيل الدخول",
                    color = BgDeepVoid,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Link to Register
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "ليس لديك حساب؟ ", color = TextSecondary, fontSize = 12.sp)
            Text(
                text = "إنشاء حساب جديد",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onSwitchToRegister() }
                    .testTag("switch_to_register")
            )
        }
    }

    if (showResetDialog) {
        var resetEmail by remember { mutableStateOf(email) }
        Dialog(onDismissRequest = { showResetDialog = false }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "استعادة كلمة المرور",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "أدخل بريدك الإلكتروني وسنرسل لك رابطاً لإعادة تعيين كلمة المرور فوراً عبر Firebase.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        placeholder = { Text("البريد الإلكتروني", color = TextMuted, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = BgCardElevated,
                            unfocusedContainerColor = BgCardElevated
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showResetDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("إلغاء", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                viewModel.resetPassword(resetEmail)
                                showResetDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("إرسال", color = BgDeepVoid, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RegisterForm(
    viewModel: AuthViewModel,
    isLoading: Boolean,
    onSwitchToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Name Field
        Text(text = "اسم الأوتاكو / الاسم المستعار", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("مثال: ساموراي الظلال", color = TextMuted, fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle,
                focusedContainerColor = BgCardElevated,
                unfocusedContainerColor = BgCardElevated
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_name_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Email Field
        Text(text = "البريد الإلكتروني", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("example@domain.com", color = TextMuted, fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = NeonCyan) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle,
                focusedContainerColor = BgCardElevated,
                unfocusedContainerColor = BgCardElevated
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_email_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password Field
        Text(text = "كلمة المرور (6 أحرف فأكثر)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("••••••••", color = TextMuted, fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonCyan) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور",
                        tint = TextSecondary
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle,
                focusedContainerColor = BgCardElevated,
                unfocusedContainerColor = BgCardElevated
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_password_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password Field
        Text(text = "تأكيد كلمة المرور", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = { Text("••••••••", color = TextMuted, fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = NeonCyan) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderSubtle,
                focusedContainerColor = BgCardElevated,
                unfocusedContainerColor = BgCardElevated
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("register_confirm_password_input")
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Submit Button
        Button(
            onClick = {
                viewModel.register(name, email, password, confirmPassword)
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan,
                disabledContainerColor = NeonCyan.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("register_submit_btn")
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = BgDeepVoid,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "إنشاء الحساب الآن",
                    color = BgDeepVoid,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Link to Login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "لديك حساب بالفعل؟ ", color = TextSecondary, fontSize = 12.sp)
            Text(
                text = "تسجيل الدخول",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onSwitchToLogin() }
                    .testTag("switch_to_login")
            )
        }
    }
}

/**
 * Preserved AuthDialog for backwards compatibility with dialog-based prompts across the app.
 */
@Composable
fun AuthDialog(
    onDismiss: () -> Unit,
    onLogin: (email: String, pass: String) -> Unit,
    onRegister: (name: String, email: String, pass: String) -> Unit,
    onGuestLogin: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BgCard),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, brush = AnimeGradient, shape = RoundedCornerShape(24.dp))
                .testTag("auth_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = TextSecondary)
                    }
                }

                Text(
                    text = if (isRegisterMode) "إنشاء حساب أوتاكو جديد" else "تسجيل الدخول إلى أنمي بلاك",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "انضم إلى أقوى مجتمع أوتاكو عربي",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("الاسم المستعار في المنصة", color = TextMuted, fontSize = 12.sp) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = BgCardElevated,
                            unfocusedContainerColor = BgCardElevated
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_register_name_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("البريد الإلكتروني", color = TextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = NeonCyan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedContainerColor = BgCardElevated,
                        unfocusedContainerColor = BgCardElevated
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_auth_email_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("كلمة المرور", color = TextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonCyan) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedContainerColor = BgCardElevated,
                        unfocusedContainerColor = BgCardElevated
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_auth_password_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isRegisterMode) {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                onRegister(name.ifBlank { "أوتاكو جديد" }, email, password)
                                onDismiss()
                            }
                        } else {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                onLogin(email, password)
                                onDismiss()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("dialog_auth_submit_btn")
                ) {
                    Text(
                        text = if (isRegisterMode) "تأكيد وإنشاء الحساب" else "دخول الحساب",
                        color = BgDeepVoid,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isRegisterMode) "لديك حساب بالفعل؟ تسجيل الدخول" else "ليس لديك حساب؟ إنشاء حساب جديد",
                    color = NeonPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { isRegisterMode = !isRegisterMode }
                        .padding(4.dp)
                        .testTag("dialog_toggle_auth_mode")
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        onGuestLogin()
                        onDismiss()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "متابعة كزائر بدون تسجيل", fontSize = 12.sp)
                }
            }
        }
    }
}
