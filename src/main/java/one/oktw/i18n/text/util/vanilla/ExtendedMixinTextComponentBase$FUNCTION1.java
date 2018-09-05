package one.oktw.i18n.text.util.vanilla;

import com.google.common.base.Function;
import one.oktw.i18n.text.interfaces.IExtendedMixinTextComponent;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Locale;

public class ExtendedMixinTextComponentBase$FUNCTION1 implements Function<IExtendedMixinTextComponent, Iterator<IExtendedMixinTextComponent>>{
    Locale locale;

    public ExtendedMixinTextComponentBase$FUNCTION1(Locale loc) {
        locale = loc;
    }

    public Iterator<IExtendedMixinTextComponent> apply(@Nullable IExtendedMixinTextComponent p_apply_1_)
    {
        return p_apply_1_.iterator(locale);
    }
}
