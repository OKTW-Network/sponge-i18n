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

import one.oktw.i18n.text.interfaces.IExtendedMixinTextComponent;

import java.util.Iterator;
import java.util.Locale;

public class ExtendedTextComponentIterable implements Iterable<IExtendedMixinTextComponent> {
    private final Locale locale;
    private final IExtendedMixinTextComponent component;
    private final boolean includeSelf;

    public ExtendedTextComponentIterable(Locale locale, IExtendedMixinTextComponent component, boolean includeSelf) {
        this.component = component;
        this.includeSelf = includeSelf;
        this.locale = locale;
    }

    @Override
    public Iterator<IExtendedMixinTextComponent> iterator() {
        if (this.includeSelf) {
            return new ExtendedTextComponentIterator(locale, this.component);
        }
        return new ExtendedTextComponentIterator(locale, this.component.childrenIterator(locale));
    }

}
