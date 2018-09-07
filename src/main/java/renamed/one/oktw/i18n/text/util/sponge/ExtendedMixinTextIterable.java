package renamed.one.oktw.i18n.text.util.sponge;

import renamed.one.oktw.i18n.text.interfaces.IExtendedMixinTextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Locale;

public class ExtendedMixinTextIterable implements Iterable<IExtendedMixinTextComponent> {
    private final Locale locale;
    private final IExtendedMixinTextComponent component;

    public ExtendedMixinTextIterable(Locale newLocale, IExtendedMixinTextComponent newComponent) {
        locale = newLocale;
        component = newComponent;
    }

    @NotNull
    @Override
    public Iterator<IExtendedMixinTextComponent> iterator() {
        return component.iterator(locale);
    }
}
