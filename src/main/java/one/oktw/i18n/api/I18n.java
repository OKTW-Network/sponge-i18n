package one.oktw.i18n.api;

import one.oktw.i18n.api.provider.TranslationStringProvider;
import one.oktw.i18n.api.service.TranslationService;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.text.serializer.TextSerializer;

import java.util.Locale;

public interface I18n {
    @NotNull
    Registry getRegistry();

    @NotNull
    TextSerializer getLegacySerializer(@NotNull Locale locale);

    @NotNull
    TextSerializer getSpongeSerializer(@NotNull Locale locale);

    @NotNull
    TextSerializer getPlainTextSerializer(@NotNull Locale locale);

    @NotNull
    TranslationService register(@NotNull String scope, @NotNull TranslationStringProvider provider, boolean allowConflict);
}
