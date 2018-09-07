package renamed.one.oktw.i18n.mixin;

import renamed.one.oktw.i18n.api.I18nImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.common.text.translation.SpongeTranslation;
import java.util.Locale;

@Mixin(value = SpongeTranslation.class, remap = false)
public class MixinSpongeTranslation {
    @Final
    @Shadow
    private String id;

    @Inject(method = "get(Ljava/util/Locale;)Ljava/lang/String;", at = @At("HEAD"))
    private void onGet(Locale locale, CallbackInfoReturnable<String> cir) {
        String temp = I18nImpl.INSTANCE.getRegistry().get(locale, id);

        if (!temp.equals(id)) {
            cir.setReturnValue(temp);
        }
    }

    @Inject(method = "get(Ljava/util/Locale;[Ljava/lang/Object;)Ljava/lang/String;", at = @At("HEAD"))
    private void onGet(Locale locale, Object[] args, CallbackInfoReturnable<String> cir){
        String temp = I18nImpl.INSTANCE.getRegistry().get(locale, id);

        if (!temp.equals((id))) {
            cir.setReturnValue(String.format(locale, temp, args));
        }
    }
}
