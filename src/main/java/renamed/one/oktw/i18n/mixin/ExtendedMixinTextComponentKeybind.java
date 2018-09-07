package renamed.one.oktw.i18n.mixin;

import net.minecraft.util.text.TextComponentKeybind;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Locale;

@Mixin(value = TextComponentKeybind.class)
public abstract class ExtendedMixinTextComponentKeybind extends ExtendedMixinTextComponentBase {
    @Override
    public String getUnformattedComponentText(Locale locale) {
        return getUnformattedComponentText();
    }
}
