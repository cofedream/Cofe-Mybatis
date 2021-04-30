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

package tk.cofe.plugin.mybatis.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.settings.model.ApplicationSettings;
import tk.cofe.plugin.mybatis.ui.settings.GlobalSettingsForm;

import javax.swing.*;

/**
 * @author : zhengrf
 * @date : 2021-04-29
 */
public class SettingsConfigurable implements Configurable {
    private final SettingsService settingsService;
    private final ApplicationSettings applicationSettings;
    private GlobalSettingsForm settingsForm;

    public SettingsConfigurable() {
        settingsService = SettingsService.getInstance();
        applicationSettings = settingsService.getState();
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Cofe Mybatis";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingsForm = new GlobalSettingsForm();
        return settingsForm.getRoot();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        final SettingsService instance = SettingsService.getInstance();
    }
}
