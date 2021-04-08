/*
 * Copyright (C) 2019-2021 cofe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tk.cofe.plugin.mbel;

import com.intellij.lang.InjectableLanguage;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NonNls;

/**
 * @author : zhengrf
 * @date : 2019-10-26
 */
public class MbELLanguage extends Language implements InjectableLanguage {

    @NonNls
    public static final String ID = "MbEL";

    @NonNls
    public static final String EXPRESSION_PREFIX = "#{";
    @NonNls
    public static final String EXPRESSION_SUFFIX = "}";

    public static final MbELLanguage INSTANCE = new MbELLanguage();

    private MbELLanguage() {
        super(ID);
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }

    @Override
    public LanguageFileType getAssociatedFileType() {
        return MbELFileType.INSTANCE;
    }
}
