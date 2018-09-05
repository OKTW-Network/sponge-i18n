package one.oktw.i18n.mixin;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import net.minecraft.util.text.translation.I18n;
import one.oktw.i18n.text.interfaces.IExtendedMixinTextComponent;
import net.minecraft.util.text.*;
import one.oktw.i18n.api.I18nImpl;
import one.oktw.i18n.text.util.vanilla.ExtendedMixinTextComponentBase$FUNCTION1;
import one.oktw.i18n.text.util.vanilla.ExtendedMixinTextComponentBase$FUNCTION2;
import one.oktw.i18n.text.util.vanilla.ExtendedTextComponentIterable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(value = TextComponentTranslation.class)
public abstract class ExtendedMixinTextComponentTranslation extends ExtendedMixinTextComponentBase {
    @Shadow
    @Final
    private String key;

    @Shadow
    @Final
    private Object[] formatArgs;

    // vanilla
    private static final Pattern STRING_VARIABLE_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    private ConcurrentHashMap<Locale, List<IExtendedMixinTextComponent>> childrenExtended = new ConcurrentHashMap<>();

    public String getUnformattedComponentText(Locale locale) {
        StringBuilder stringbuilder = new StringBuilder();

        for (IExtendedMixinTextComponent itextcomponent : this.getChildrenByLocale(locale))
        {
            stringbuilder.append(itextcomponent.getUnformattedComponentText(locale));
        }

        return stringbuilder.toString();
    }

    private List<IExtendedMixinTextComponent> getChildrenByLocale(Locale locale) {
        if (childrenExtended.containsKey(locale)) {
            return childrenExtended.get(locale);
        }

        List<IExtendedMixinTextComponent> newChildren = createChildren(locale);

        childrenExtended.put(locale, newChildren);

        return newChildren;
    }

    @SuppressWarnings("deprecation")
    private List<IExtendedMixinTextComponent> createChildren(Locale locale) {
        String format;

        if (key.contains(":")) {
            format = I18nImpl.INSTANCE.getRegistry().get(locale, key);
        } else {
            format = I18nImpl.INSTANCE.getRegistry().get(locale, "minecraft:" + key);
        }

        if (format.equals(key)) {
            try {
                format = I18n.translateToLocal(this.key);
            } catch (Exception err) {
                format = I18n.translateToFallback(this.key);
            }
        }

        List<ITextComponent> children = Lists.<ITextComponent>newArrayList();

        boolean flag = false;

        Matcher matcher = STRING_VARIABLE_PATTERN.matcher(format);
        int i = 0;
        int j = 0;

        try {
            int l;

            for (; matcher.find(j); j = l) {
                int k = matcher.start();
                l = matcher.end();

                if (k > j) {
                    TextComponentString textcomponentstring = new TextComponentString(String.format(format.substring(j, k)));
                    textcomponentstring.getStyle().setParentStyle(this.getStyle());
                    children.add(textcomponentstring);
                }

                String s2 = matcher.group(2);
                String s = format.substring(k, l);

                if ("%".equals(s2) && "%%".equals(s)) {
                    TextComponentString textcomponentstring2 = new TextComponentString("%");
                    textcomponentstring2.getStyle().setParentStyle(this.getStyle());
                    children.add(textcomponentstring2);
                } else {
                    if (!"s".equals(s2)) {
                        throw new TextComponentTranslationFormatException((TextComponentTranslation) (Object) this, "Unsupported format: '" + s + "'");
                    }

                    String s1 = matcher.group(1);
                    int i1 = s1 != null ? Integer.parseInt(s1) - 1 : i++;

                    if (i1 < this.formatArgs.length) {
                        children.add(this.getFormatArgumentAsComponent(i1));
                    }
                }
            }

            if (j < format.length()) {
                TextComponentString textcomponentstring1 = new TextComponentString(String.format(format.substring(j)));
                textcomponentstring1.getStyle().setParentStyle(this.getStyle());
                children.add(textcomponentstring1);
            }
        } catch (IllegalFormatException illegalformatexception) {
            throw new TextComponentTranslationFormatException((TextComponentTranslation) (Object) this, illegalformatexception);
        }

        return (List<IExtendedMixinTextComponent>) (Object) children;
    }

    public Iterator<IExtendedMixinTextComponent> iterator(Locale locale) {
        return Iterators.<IExtendedMixinTextComponent>concat(createDeepCopyIterator(locale, this.getChildrenByLocale(locale)), createDeepCopyIterator(locale, (Iterable<IExtendedMixinTextComponent>) (Object) this.siblings));
    }

    @Shadow
    private ITextComponent getFormatArgumentAsComponent(int index) {
        return null;
    }

    @Inject(method = "setStyle(Lnet/minecraft/util/text/Style;)Lnet/minecraft/util/text/ITextComponent;", at = @At("HEAD"))
    private void onSetStyle(Style style, CallbackInfoReturnable<ITextComponent> cir) {
        childrenExtended.forEach((locale, children) -> {
            for (ITextComponent itextcomponent : children)
            {
                itextcomponent.getStyle().setParentStyle(style);
            }
        });
    }

    // sponge
    public Iterable<IExtendedMixinTextComponent> withChildren(Locale locale) {
        return new ExtendedTextComponentIterable(locale, this, false);
    }

    public Iterator<IExtendedMixinTextComponent> childrenIterator(Locale locale) {
        return Iterators.concat(this.getChildrenByLocale(locale).iterator(), super.childrenIterator(locale));
    }

    private static Iterator<IExtendedMixinTextComponent> createDeepCopyIterator(Locale locale, Iterable<IExtendedMixinTextComponent> components) {
        Iterator<IExtendedMixinTextComponent> iterator = Iterators.concat(Iterators.transform(components.iterator(), new ExtendedMixinTextComponentBase$FUNCTION1(locale)));
        iterator = Iterators.transform(iterator, new ExtendedMixinTextComponentBase$FUNCTION2(locale));
        return iterator;
    }
}
