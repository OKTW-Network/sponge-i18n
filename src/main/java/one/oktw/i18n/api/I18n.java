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
    TextSerializer getLegacySerializer(@NotNull Locale var1);

    @NotNull
    TextSerializer getSpongeSerializer(@NotNull Locale var1);

    @NotNull
    TextSerializer getPlainTextSerializer(@NotNull Locale var1);

    @NotNull
    TranslationService register(@NotNull String var1, @NotNull TranslationStringProvider var2, boolean var3);

}
