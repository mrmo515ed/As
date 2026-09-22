package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.theme.*

data class HeroFighter(
    val name: String,
    val title: String,
    val hpMax: Int,
    val attackPower: Int,
    val ultimateName: String,
    val avatarEmoji: String
)

data class EnemyFighter(
    val name: String,
    val hpMax: Int,
    val attackPower: Int,
    val avatarEmoji: String
)

@Composable
fun GamesHubScreen(
    user: User?,
    onRewardEarned: (coins: Int, stars: Int, xp: Int) -> Unit
) {
    val heroes = listOf(
        HeroFighter("لوفي نيكا", "إمبراطور البحر", 1000, 160, "باجرانغ غن 🔥", "👒"),
        HeroFighter("زورو", "ملك الجحيم", 900, 180, "أشورا: تسعة سيوف ⚔️", "🗡️"),
        HeroFighter("غوجو ساتورو", "الأقوى بلا منازع", 1100, 200, "توسع النطاق: الفراغ اللانهائي 🌀", "🤞"),
        HeroFighter("تانجيرو", "مستخدم تنفس الشمس", 850, 140, "رقصة إله النار ☀️", "🎴")
    )

    val enemies = listOf(
        EnemyFighter("سكونا: ملك اللعنات", 1200, 150, "👹"),
        EnemyFighter("موزان كيبوتسوجي", 1000, 130, "🩸"),
        EnemyFighter("كايدو: أقوى مخلوق", 1400, 170, "🐉"),
        EnemyFighter("مادارا أوتشيها", 1300, 160, "👁️")
    )

    var selectedHeroIndex by remember { mutableStateOf(0) }
    var currentEnemyIndex by remember { mutableStateOf(0) }

    val currentHero = heroes[selectedHeroIndex]
    val currentEnemy = enemies[currentEnemyIndex]

    var heroHp by remember { mutableStateOf(currentHero.hpMax) }
    var enemyHp by remember { mutableStateOf(currentEnemy.hpMax) }
    var ultCooldown by remember { mutableStateOf(0) }
    var battleLog by remember { mutableStateOf(listOf("بدأت معركة الأبطال! اختر هجومك بحكمة.")) }
    var isVictory by remember { mutableStateOf(false) }
    var isDefeat by remember { mutableStateOf(false) }

    fun resetBattle(newHeroIndex: Int = selectedHeroIndex, newEnemyIndex: Int = currentEnemyIndex) {
        selectedHeroIndex = newHeroIndex
        currentEnemyIndex = newEnemyIndex
        heroHp = heroes[newHeroIndex].hpMax
        enemyHp = enemies[newEnemyIndex].hpMax
        ultCooldown = 0
        battleLog = listOf("بدأت جولة جديدة ضد ${enemies[newEnemyIndex].name}!")
        isVictory = false
        isDefeat = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeepVoid)
            .testTag("games_hub_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Hero Selector Row
        item {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "اختر بطلك للقتال:",
                    color = NeonCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(heroes.indices.toList()) { idx ->
                        val h = heroes[idx]
                        val isSel = selectedHeroIndex == idx
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSel) Color(0x3300F0FF) else BgCard),
                            modifier = Modifier
                                .border(1.dp, if (isSel) NeonCyan else BorderSubtle, RoundedCornerShape(12.dp))
                                .clickable { resetBattle(newHeroIndex = idx) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = h.avatarEmoji, fontSize = 20.sp)
                                Column {
                                    Text(text = h.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(text = h.title, color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Battle Arena Canvas
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .border(1.5.dp, brush = FireGradient, shape = RoundedCornerShape(20.dp))
                    .testTag("battle_arena_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Arena Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ساحة أنمي بلاك القتالية 🔥",
                            color = FlameOrange,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x33FB7185))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = "مستوى التحدي S+", color = CrimsonRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fighters VS View
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hero Side
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(BgCardElevated)
                                    .border(2.dp, NeonCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = currentHero.avatarEmoji, fontSize = 30.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = currentHero.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "HP: $heroHp / ${currentHero.hpMax}", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            val heroHpRatio = (heroHp.toFloat() / currentHero.hpMax).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { heroHpRatio },
                                modifier = Modifier
                                    .width(90.dp)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = NeonCyan,
                                trackColor = BgSurfaceDark
                            )
                        }

                        // VS Badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FB7185))
                                .border(1.dp, CrimsonRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "VS", color = CrimsonRed, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }

                        // Enemy Side
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(BgCardElevated)
                                    .border(2.dp, CrimsonRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = currentEnemy.avatarEmoji, fontSize = 30.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = currentEnemy.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "HP: $enemyHp / ${currentEnemy.hpMax}", color = CrimsonRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            val enemyHpRatio = (enemyHp.toFloat() / currentEnemy.hpMax).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { enemyHpRatio },
                                modifier = Modifier
                                    .width(90.dp)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CrimsonRed,
                                trackColor = BgSurfaceDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Combat Actions or Victory Screen
                    if (isVictory) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x2210B981))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🏆 انتصار ساحق!", color = EmeraldGreen, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Text(text = "حصلت على مكافآت الشرف: +100 عملة | +5 نجوم | +150 XP", color = OtakuGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    val nextEnemy = (currentEnemyIndex + 1) % enemies.size
                                    resetBattle(newEnemyIndex = nextEnemy)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                            ) {
                                Text(text = "التالي: قتال الوحش القادم ⚔️", color = BgDeepVoid, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (isDefeat) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x22FB7185))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "💀 سقطت في المعركة!", color = CrimsonRed, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text(text = "قم بترقية بطلك وحاول مجدداً.", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { resetBattle() },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                            ) {
                                Text(text = "إعادة المحاولة", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Basic Strike
                            Button(
                                onClick = {
                                    val damageDealt = currentHero.attackPower + (-20..30).random()
                                    val newEnemyHp = (enemyHp - damageDealt).coerceAtLeast(0)
                                    enemyHp = newEnemyHp

                                    if (ultCooldown > 0) ultCooldown--

                                    if (newEnemyHp <= 0) {
                                        isVictory = true
                                        onRewardEarned(100, 5, 150)
                                        battleLog = listOf("وجهت ضربة قاضية وحققت النصر!") + battleLog
                                    } else {
                                        // Enemy Counter Attack
                                        val enemyDamage = currentEnemy.attackPower + (-15..25).random()
                                        val newHeroHp = (heroHp - enemyDamage).coerceAtLeast(0)
                                        heroHp = newHeroHp

                                        if (newHeroHp <= 0) {
                                            isDefeat = true
                                            battleLog = listOf("سقطت أمام هجوم العدو المضاد!") + battleLog
                                        } else {
                                            battleLog = listOf(
                                                "هاجمت مسبباً $damageDealt ضرر! العدو رد بهجوم سبب لك $enemyDamage ضرر."
                                            ) + battleLog
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("basic_attack_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Attack", tint = BgDeepVoid, modifier = Modifier.size(16.dp))
                                    Text(text = "هجوم عادي", color = BgDeepVoid, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            // Ultimate Skill
                            Button(
                                onClick = {
                                    if (ultCooldown == 0) {
                                        val ultDamage = (currentHero.attackPower * 2.8).toInt()
                                        val newEnemyHp = (enemyHp - ultDamage).coerceAtLeast(0)
                                        enemyHp = newEnemyHp
                                        ultCooldown = 3

                                        if (newEnemyHp <= 0) {
                                            isVictory = true
                                            onRewardEarned(150, 10, 250)
                                            battleLog = listOf("أطلقت ${currentHero.ultimateName} ومحقت العدو تماماً!") + battleLog
                                        } else {
                                            val enemyDamage = (currentEnemy.attackPower * 0.7).toInt()
                                            val newHeroHp = (heroHp - enemyDamage).coerceAtLeast(0)
                                            heroHp = newHeroHp

                                            if (newHeroHp <= 0) {
                                                isDefeat = true
                                            } else {
                                                battleLog = listOf("أطلقت ${currentHero.ultimateName}! سبب $ultDamage ضرر ساحق!") + battleLog
                                            }
                                        }
                                    }
                                },
                                enabled = ultCooldown == 0,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonPurple,
                                    disabledContainerColor = BgCardElevated
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ultimate_attack_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Bolt, contentDescription = "Ultimate", tint = Color.White, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = if (ultCooldown == 0) "المهارة القصوى" else "شحن ($ultCooldown)",
                                        color = if (ultCooldown == 0) Color.White else TextMuted,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Battle Log
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BgDeepVoid)
                            .padding(10.dp)
                    ) {
                        Text(text = "سجل أحداث المعركة:", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        battleLog.take(3).forEach { log ->
                            Text(text = "• $log", color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
