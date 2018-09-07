package one.oktw.i18n.api;

import one.oktw.i18n.api.provider.TranslationStringProvider;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Locale;

public interface Registry {
    void register(@NotNull TranslationStringProvider provider);

    @NotNull
    String get(@NotNull Locale locale, @NotNull String key);

    @NotNull
    void setLanguage(@NotNull Player player, @NotNull Locale locale);

    @NotNull
    Locale getLanguage(@NotNull Player player);
}
