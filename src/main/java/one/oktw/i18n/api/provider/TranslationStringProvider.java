package one.oktw.i18n.api.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface TranslationStringProvider {
    @Nullable
    String get(@NotNull Locale var1, @NotNull String var2);
}
