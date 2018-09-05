package one.oktw.i18n.mixin;

import net.minecraft.util.text.TextComponentScore;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Locale;

@Mixin(value = TextComponentScore.class)
public abstract class ExtendedMixinTextComponentScore extends ExtendedMixinTextComponentBase {
    @Override
    public String getUnformattedComponentText(Locale locale) {
        return getUnformattedComponentText();
    }
}
