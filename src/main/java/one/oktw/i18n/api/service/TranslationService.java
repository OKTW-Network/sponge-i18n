package one.oktw.i18n.api.service;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.Translation;

import java.util.Locale;

public interface TranslationService {

    @NotNull
    Text fromItem(@NotNull ItemStack var1);

    @NotNull
    Text ofLiteralPlaceHolder(@NotNull Object... var1);

    @NotNull
    Text ofPlaceHolder(@NotNull Object... var1);

    @NotNull
    Text ofPlaceHolder(@NotNull String var1, @NotNull Object... var2);

    @NotNull
    Text ofPlaceHolderUnscoped(@NotNull String var1, @NotNull Object... var2);

    @NotNull
    Text of(@NotNull String var1, @NotNull Object... var2);

    @NotNull
    Text ofUnscoped(@NotNull String var1, @NotNull Object... var2);

    @NotNull
    Translation translation(@NotNull String var1);

    @NotNull
    Translation translationUnscoped(@NotNull String var1);

    @NotNull
    String toPlain(@NotNull Player var1, @NotNull Text var2);

    @NotNull
    String toPlain(@NotNull Locale var1, @NotNull Text var2);

    @NotNull
    String toLegacy(@NotNull Player var1, @NotNull Text var2);

    @NotNull
    String toLegacy(@NotNull Locale var1, @NotNull Text var2);

    @NotNull
    String toSponge(@NotNull Player var1, @NotNull Text var2);

    @NotNull
    String toSponge(@NotNull Locale var1, @NotNull Text var2);

    @NotNull
    Text removeStyle(@NotNull Text var1);
}
