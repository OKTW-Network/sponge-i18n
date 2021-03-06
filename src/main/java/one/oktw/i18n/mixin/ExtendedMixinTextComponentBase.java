package one.oktw.i18n.mixin;

import com.google.common.collect.Iterators;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextFormatting;
import one.oktw.i18n.text.interfaces.IExtendedMixinTextComponent;
import one.oktw.i18n.text.util.sponge.ExtendedMixinTextIterable;
import one.oktw.i18n.text.util.vanilla.ExtendedMixinTextComponentBase$FUNCTION1;
import one.oktw.i18n.text.util.vanilla.ExtendedMixinTextComponentBase$FUNCTION2;
import one.oktw.i18n.text.util.vanilla.ExtendedTextComponentIterable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.text.ResolvedChatStyle;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static net.minecraft.util.text.TextFormatting.*;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.spongepowered.common.text.SpongeTexts.COLOR_CHAR;

@Mixin(value = TextComponentBase.class)
public abstract class ExtendedMixinTextComponentBase implements IExtendedMixinTextComponent {
    @Shadow
    private Style style;
    @Shadow
    protected List<ITextComponent> siblings;

    // vanilla
    @Override
    public Iterable<IExtendedMixinTextComponent> iterable(Locale locale) {
        return new ExtendedMixinTextIterable(locale, this);
    }

    @Override
    public Iterator<IExtendedMixinTextComponent> iterator(Locale locale) {
        return Iterators.<IExtendedMixinTextComponent>concat(Iterators.forArray(this), i18nCreateDeepCopyIterator(locale, (List<IExtendedMixinTextComponent>) (Object) this.siblings));
    }

    public String getUnformattedComponentText(Locale locale) {
        return getUnformattedComponentText();
    }

    @Override
    public String getUnformattedText(Locale locale) {
        StringBuilder stringbuilder = new StringBuilder();

        for (IExtendedMixinTextComponent itextcomponent : this.iterable(locale)) {
            stringbuilder.append(itextcomponent.getUnformattedComponentText(locale));
        }

        return stringbuilder.toString();
    }

    public String getFormattedText(Locale locale) {
        StringBuilder stringbuilder = new StringBuilder();

        for (IExtendedMixinTextComponent itextcomponent : this.iterable(locale)) {
            String s = itextcomponent.getUnformattedComponentText();

            if (!s.isEmpty()) {
                stringbuilder.append(itextcomponent.getStyle().getFormattingCode());
                stringbuilder.append(s);
                stringbuilder.append((Object) TextFormatting.RESET);
            }
        }

        return stringbuilder.toString();
    }

    private static ResolvedChatStyle i18nResolve(ResolvedChatStyle current, Style previous, Style style) {
        if (current != null && style.parentStyle == previous) {
            return new ResolvedChatStyle(
                    defaultIfNull(style.color, current.color),
                    i18nFirstNonNull(style.bold, current.bold),
                    i18nFirstNonNull(style.italic, current.italic),
                    i18nFirstNonNull(style.underlined, current.underlined),
                    i18nFirstNonNull(style.strikethrough, current.strikethrough),
                    i18nFirstNonNull(style.obfuscated, current.obfuscated)
            );
        }
        return new ResolvedChatStyle(
                style.getColor(),
                style.getBold(),
                style.getItalic(),
                style.getUnderlined(),
                style.getStrikethrough(),
                style.getObfuscated()
        );
    }

    private static boolean i18nFirstNonNull(Boolean b1, boolean b2) {
        return b1 != null ? b1 : b2;
    }

    private static void i18nApply(StringBuilder builder, char code, TextFormatting formatting) {
        builder.append(code).append(formatting.formattingCode);
    }

    private static void i18nApply(StringBuilder builder, char code, TextFormatting formatting, boolean state) {
        if (state) {
            i18nApply(builder, code, formatting);
        }
    }

    private static Iterator<IExtendedMixinTextComponent> i18nCreateDeepCopyIterator(Locale locale, Iterable<IExtendedMixinTextComponent> components) {
        Iterator<IExtendedMixinTextComponent> iterator = Iterators.concat(Iterators.transform(components.iterator(), new ExtendedMixinTextComponentBase$FUNCTION1(locale)));
        iterator = Iterators.transform(iterator, new ExtendedMixinTextComponentBase$FUNCTION2(locale));
        return iterator;
    }

    // sponge

    private StringBuilder getLegacyFormattingBuilder() {
        StringBuilder builder = new StringBuilder();

        Style style = getStyle();
        i18nApply(builder, COLOR_CHAR, defaultIfNull(style.getColor(), RESET));
        i18nApply(builder, COLOR_CHAR, BOLD, style.getBold());
        i18nApply(builder, COLOR_CHAR, ITALIC, style.getItalic());
        i18nApply(builder, COLOR_CHAR, UNDERLINE, style.getUnderlined());
        i18nApply(builder, COLOR_CHAR, STRIKETHROUGH, style.getStrikethrough());
        i18nApply(builder, COLOR_CHAR, OBFUSCATED, style.getObfuscated());

        return builder;
    }

    public String toPlain(Locale locale) {
        StringBuilder builder = new StringBuilder();

        for (IExtendedMixinTextComponent component : withChildren(locale)) {
            builder.append(component.getUnformattedComponentText(locale));
        }

        return builder.toString();
    }

    public String getLegacyFormatting(Locale locale) {
        return getLegacyFormattingBuilder().toString();
    }

    public String toLegacy(Locale locale, char code) {
        StringBuilder builder = new StringBuilder();

        ResolvedChatStyle current = null;
        Style previous = null;

        for (IExtendedMixinTextComponent component : withChildren(locale)) {
            Style newStyle = component.getStyle();
            ResolvedChatStyle style = i18nResolve(current, previous, newStyle);
            previous = newStyle;

            if (current == null
                    || (current.color != style.color)
                    || (current.bold && !style.bold)
                    || (current.italic && !style.italic)
                    || (current.underlined && !style.underlined)
                    || (current.strikethrough && !style.strikethrough)
                    || (current.obfuscated && !style.obfuscated)) {

                if (style.color != null) {
                    i18nApply(builder, code, style.color);
                } else if (current != null) {
                    i18nApply(builder, code, RESET);
                }

                i18nApply(builder, code, BOLD, style.bold);
                i18nApply(builder, code, ITALIC, style.italic);
                i18nApply(builder, code, UNDERLINE, style.underlined);
                i18nApply(builder, code, STRIKETHROUGH, style.strikethrough);
                i18nApply(builder, code, OBFUSCATED, style.obfuscated);
            } else {
                i18nApply(builder, code, BOLD, current.bold != style.bold);
                i18nApply(builder, code, ITALIC, current.italic != style.italic);
                i18nApply(builder, code, UNDERLINE, current.underlined != style.underlined);
                i18nApply(builder, code, STRIKETHROUGH, current.strikethrough != style.strikethrough);
                i18nApply(builder, code, OBFUSCATED, current.obfuscated != style.obfuscated);
            }

            current = style;

            builder.append(component.getUnformattedComponentText(locale));
        }

        return builder.toString();
    }

    public String toLegacySingle(Locale locale, char code) {
        return getLegacyFormattingBuilder()
                .append(getUnformattedComponentText(locale))
                .toString();
    }

    public Iterable<IExtendedMixinTextComponent> withChildren(Locale locale) {
        return new ExtendedTextComponentIterable(locale, this, true);
    }

    public Iterator<IExtendedMixinTextComponent> childrenIterator(Locale locale) {
        return (Iterator<IExtendedMixinTextComponent>) (Object) getSiblings().iterator();
    }
}
