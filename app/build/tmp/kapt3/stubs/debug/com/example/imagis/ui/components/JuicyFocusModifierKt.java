package com.example.imagis.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000$\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u001a\n\u0010\r\u001a\u00020\u000e*\u00020\u000e\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0010\u0010\u0003\u001a\u00020\u0002X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0004\"\u0010\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0007\"\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0010\u0010\n\u001a\u00020\u0002X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0004\"\u0010\u0010\u000b\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0007\"\u0010\u0010\f\u001a\u00020\u0002X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0004\u00a8\u0006\u000f"}, d2 = {"BorderColorSpec", "Landroidx/compose/animation/core/SpringSpec;", "Landroidx/compose/ui/graphics/Color;", "FocusedBorderColor", "J", "FocusedBorderWidth", "Landroidx/compose/ui/unit/Dp;", "F", "JuicySpring", "", "ShadowColor", "ShadowOffsetDp", "UnfocusedBorderColor", "juicyBrutalistFocus", "Landroidx/compose/ui/Modifier;", "app_debug"})
public final class JuicyFocusModifierKt {
    
    /**
     * Heavy snap: high stiffness, medium damping → elastic punch.
     */
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.animation.core.SpringSpec<java.lang.Float> JuicySpring = null;
    
    /**
     * Border color fades in fast but doesn't need bounce.
     */
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.animation.core.SpringSpec<androidx.compose.ui.graphics.Color> BorderColorSpec = null;
    
    /**
     * The offset shadow shifts right and down by this amount.
     */
    private static final float ShadowOffsetDp = 0.0F;
    
    /**
     * Shadow color: Neon Lime, fully opaque, hard edge.
     */
    private static final long ShadowColor = 0L;
    private static final float FocusedBorderWidth = 0.0F;
    private static final long FocusedBorderColor = 0L;
    private static final long UnfocusedBorderColor = 0L;
    
    /**
     * A Compose TV focus modifier implementing 'Juicy Brutalism' physics.
     *
     * Attach this to any composable that participates in D-Pad focus
     * navigation. It handles focus + press states internally.
     *
     * ```kotlin
     * Box(
     *    modifier = Modifier
     *        .size(200.dp)
     *        .juicyBrutalistFocus()
     * ) { ... }
     * ```
     */
    @org.jetbrains.annotations.NotNull()
    public static final androidx.compose.ui.Modifier juicyBrutalistFocus(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier $this$juicyBrutalistFocus) {
        return null;
    }
}