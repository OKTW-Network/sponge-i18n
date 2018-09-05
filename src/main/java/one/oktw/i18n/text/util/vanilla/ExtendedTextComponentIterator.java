/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package one.oktw.i18n.text.util.vanilla;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.util.text.ITextComponent;
import one.oktw.i18n.text.interfaces.IExtendedMixinTextComponent;
import org.spongepowered.common.interfaces.text.IMixinTextComponent;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkNotNull;

public class ExtendedTextComponentIterator extends UnmodifiableIterator<IExtendedMixinTextComponent> {

    private final Locale locale;
    private IExtendedMixinTextComponent component;
    private Iterator<IExtendedMixinTextComponent> children;
    @Nullable private Iterator<IExtendedMixinTextComponent> currentChildIterator;

    public ExtendedTextComponentIterator(Locale locale, IExtendedMixinTextComponent component) {
        this.component = checkNotNull(component, "component");
        this.locale = locale;
    }

    public ExtendedTextComponentIterator(Locale locale, Iterator<IExtendedMixinTextComponent> children) {
        this.children = checkNotNull(children, "children");
        if (this.children.hasNext()) {
            this.setCurrentChildIterator();
        }
        this.locale = locale;
    }

    @Override
    public boolean hasNext() {
        return this.component != null || (this.currentChildIterator != null && this.currentChildIterator.hasNext());
    }

    // In order for this method to work properly, 'currentChildIterator' must be ready to return an element
    // (i.e its 'hasNext()' method returns true) when this method returns. If this condition can no longer be met,
    // we're done iterating.
    @Override
    public IExtendedMixinTextComponent next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        if (this.component != null) {
            return (IExtendedMixinTextComponent)this.init();
        }

        IExtendedMixinTextComponent result = this.currentChildIterator.next();

        if (!this.currentChildIterator.hasNext() && this.children.hasNext()) {
            this.setCurrentChildIterator();
        }

        return result;
    }

    private ITextComponent init() {
        this.children = this.component.childrenIterator(locale);

        ITextComponent result = this.component;
        this.component = null;

        // An iterator of an empty TextComponentTranslation doesn't have children. Thus, calling 'this.currentChildIterator.next()'
        // at the end of this method will lead to a NoSuchElementException. To fix this, we
        // initialize currentChildIterator so that the following call to 'hasNext()' will properly return 'false' if necessary
        if (this.children.hasNext()) {
            this.setCurrentChildIterator();
        }

        return result;
    }

    private void setCurrentChildIterator() {
        this.currentChildIterator = ((IExtendedMixinTextComponent) this.children.next()).withChildren(locale).iterator();
    }

}
