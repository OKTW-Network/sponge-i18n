package one.oktw.i18n.mixin;

import net.minecraft.util.text.TextComponentString;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Locale;

@Mixin(value = TextComponentString.class)
public abstract class ExtendedMixinTextComponentString extends ExtendedMixinTextComponentBase {
    @Override
    public String getUnformattedComponentText(Locale locale) {
        return getUnformattedComponentText();
    }
}
