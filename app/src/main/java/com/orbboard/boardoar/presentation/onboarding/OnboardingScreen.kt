package com.orbboard.boardoar.presentation.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.orbboard.boardoar.data.preferences.AppPreferences
import com.orbboard.boardoar.ui.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private data class OnboardingPage(
    val title: String,
    val description: String,
    val primaryColor: Color,
    val orbColors: List<Color>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    preferences: AppPreferences = koinInject()
) {
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            "Organize your\nideas visually",
            "Plan tasks using a beautiful visual board where every idea becomes an orb.",
            NeonBlue,
            listOf(NeonBlue, NeonPink, NeonPurple, NeonYellow, NeonLime)
        ),
        OnboardingPage(
            "Group tasks into\ncategories",
            "Color-coded orbs for Work, Personal, Ideas, Shopping, and Goals.",
            NeonPurple,
            listOf(NeonPurple, NeonBlue, NeonPink, NeonLime, NeonYellow)
        ),
        OnboardingPage(
            "Track progress\neasily",
            "Watch your board fill with completed orbs as you achieve your goals.",
            NeonLime,
            listOf(NeonLime, NeonBlue, NeonYellow, NeonPink, NeonPurple)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, BackgroundDark2)
                )
            )
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated dot indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 36.dp)
            ) {
                repeat(pages.size) { index ->
                    val width by animateDpAsState(
                        targetValue = if (pagerState.currentPage == index) 28.dp else 8.dp,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "dot_width"
                    )
                    val dotColor by animateColorAsState(
                        targetValue = if (pagerState.currentPage == index)
                            pages[pagerState.currentPage].primaryColor
                        else TextTertiary,
                        label = "dot_color"
                    )
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }

            if (pagerState.currentPage < pages.size - 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                preferences.setOnboardingCompleted(true)
                                onFinished()
                            }
                        }
                    ) {
                        Text("Skip", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = pages[pagerState.currentPage].primaryColor
                        ),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text(
                            "Next",
                            color = BackgroundDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            preferences.setOnboardingCompleted(true)
                            onFinished()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                ) {
                    Text(
                        "Start Organizing",
                        color = BackgroundDark,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 32.dp)
            .padding(top = 60.dp, bottom = 200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OrbsIllustration(
            colors = page.orbColors,
            modifier = Modifier
                .size(260.dp)
                .padding(bottom = 48.dp)
        )

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )
    }
}

@Composable
private fun OrbsIllustration(
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_float")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val positions = listOf(
        Pair(0.5f, 0.22f),
        Pair(0.22f, 0.52f),
        Pair(0.78f, 0.48f),
        Pair(0.38f, 0.76f),
        Pair(0.65f, 0.72f)
    )
    val radii = listOf(52f, 38f, 44f, 32f, 36f)

    androidx.compose.foundation.Canvas(modifier = modifier) {
        colors.forEachIndexed { i, color ->
            val (px, py) = positions[i]
            val floatOffset = if (i % 2 == 0) floatAnim * 8f else -(floatAnim * 8f)
            val cx = size.width * px
            val cy = size.height * py + floatOffset
            val r = radii[i]

            // Glow
            for (g in 4 downTo 1) {
                drawCircle(
                    color = color.copy(alpha = 0.06f * (5 - g)),
                    radius = r + g * 8f,
                    center = Offset(cx, cy)
                )
            }

            // Body
            val lightC = Color(
                red = (color.red + (1f - color.red) * 0.3f).coerceAtMost(1f),
                green = (color.green + (1f - color.green) * 0.3f).coerceAtMost(1f),
                blue = (color.blue + (1f - color.blue) * 0.3f).coerceAtMost(1f),
                alpha = 1f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(lightC, color, color.copy(red = color.red * 0.5f, green = color.green * 0.5f, blue = color.blue * 0.5f)),
                    center = Offset(cx - r * 0.25f, cy - r * 0.3f),
                    radius = r * 2f
                ),
                radius = r,
                center = Offset(cx, cy)
            )
            // Highlight
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.7f), Color.Transparent),
                    center = Offset(cx - r * 0.28f, cy - r * 0.32f),
                    radius = r * 0.38f
                ),
                radius = r * 0.38f,
                center = Offset(cx - r * 0.28f, cy - r * 0.32f)
            )
        }
    }
}
