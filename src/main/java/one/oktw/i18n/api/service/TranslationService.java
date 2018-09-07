package one.oktw.i18n.api.service;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.translation.Translation;

import java.util.Locale;

@SuppressWarnings("unused")
public interface TranslationService {
    @NotNull
    Text ofLiteralPlaceHolder(@NotNull Object... args);

    @NotNull
    Text ofPlaceHolder(@NotNull Object... args);

    @NotNull
    Text ofPlaceHolder(@NotNull String key, @NotNull Object... args);

    @NotNull
    Text ofPlaceHolderUnscoped(@NotNull String key, @NotNull Object... args);

    @NotNull
    Text of(@NotNull String key, @NotNull Object... args);

    @NotNull
    Text ofUnscoped(@NotNull String key, @NotNull Object... args);

    @NotNull
    Translation translation(@NotNull String key);

    @NotNull
    Translation translationUnscoped(@NotNull String key);

    @NotNull
    String toPlain(@NotNull Player player, @NotNull Text text);

    @NotNull
    String toPlain(@NotNull Locale locale, @NotNull Text text);

    @NotNull
    String toLegacy(@NotNull Player player, @NotNull Text text);

    @NotNull
    String toLegacy(@NotNull Locale locale, @NotNull Text text);

    @NotNull
    String toSponge(@NotNull Player player, @NotNull Text text);

    @NotNull
    String toSponge(@NotNull Locale locale, @NotNull Text text);

    // get item title of either a place holder, a text, a vanilla translation
    @NotNull
    Text fromItem(@NotNull ItemStack itemStack);

    // unpack Text from either a placeholder or return itself if it is not a place holder
    @NotNull
    Text fromText(@NotNull Text text);

    // unpack Text from either a string or just return Text wrapper of itself
    @NotNull
    Text fromString(@NotNull String str);

    // utility function to remove all style recursive of a text without breaking translation keys
    @NotNull
    Text removeStyle(@NotNull Text text);
}
