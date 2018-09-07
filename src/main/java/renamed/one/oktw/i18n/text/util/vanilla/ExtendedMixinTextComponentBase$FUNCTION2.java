package renamed.one.oktw.i18n.text.util.vanilla;

import com.google.common.base.Function;
import renamed.one.oktw.i18n.text.interfaces.IExtendedMixinTextComponent;

import javax.annotation.Nullable;
import java.util.Locale;

public class ExtendedMixinTextComponentBase$FUNCTION2 implements Function<IExtendedMixinTextComponent, IExtendedMixinTextComponent>{
    Locale locale;

    public ExtendedMixinTextComponentBase$FUNCTION2(Locale loc) {
        locale = loc;
    }

    public IExtendedMixinTextComponent apply(@Nullable IExtendedMixinTextComponent p_apply_1_)
    {
        IExtendedMixinTextComponent itextcomponent = (IExtendedMixinTextComponent)p_apply_1_.createCopy();
        itextcomponent.setStyle(itextcomponent.getStyle().createDeepCopy());
        return itextcomponent;
    }
}
