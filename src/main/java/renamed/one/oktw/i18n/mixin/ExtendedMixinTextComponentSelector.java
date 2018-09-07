package renamed.one.oktw.i18n.mixin;

import net.minecraft.util.text.TextComponentSelector;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Locale;

@Mixin(value = TextComponentSelector.class)
public abstract class ExtendedMixinTextComponentSelector extends ExtendedMixinTextComponentBase {
    @Override
    public String getUnformattedComponentText(Locale locale) {
        return getUnformattedComponentText();
    }
}
