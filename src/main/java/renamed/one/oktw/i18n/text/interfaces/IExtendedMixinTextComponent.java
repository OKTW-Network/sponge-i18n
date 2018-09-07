package renamed.one.oktw.i18n.text.interfaces;

import org.spongepowered.common.interfaces.text.IMixinTextComponent;

import java.util.Iterator;
import java.util.Locale;

public interface IExtendedMixinTextComponent extends IMixinTextComponent {
    // vanilla
    Iterable<IExtendedMixinTextComponent> iterable(Locale locale);

    Iterator<IExtendedMixinTextComponent> iterator(Locale locale);

    String getUnformattedComponentText(Locale locale);

    String getUnformattedText(Locale locale);

    String getFormattedText(Locale locale);

    // sponge
    Iterable<IExtendedMixinTextComponent> withChildren(Locale locale);

    Iterator<IExtendedMixinTextComponent> childrenIterator(Locale locale);

    String toPlain(Locale locale);

    String getLegacyFormatting(Locale locale);

    String toLegacy(Locale locale, char code);

    String toLegacySingle(Locale locale, char code);
}
