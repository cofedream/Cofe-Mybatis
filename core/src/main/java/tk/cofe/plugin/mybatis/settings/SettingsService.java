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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.cofe.plugin.mybatis.settings.model.ApplicationSettings;

/**
 * @author : zhengrf
 * @date : 2021-04-29
 */
@State(name = "CofeMybatisConfig", storages = {@Storage("cofeMybatisConfig.xml")})
public class SettingsService implements PersistentStateComponent<ApplicationSettings> {

    private ApplicationSettings settings = new ApplicationSettings();

    public static SettingsService getInstance() {
        return ApplicationManager.getApplication().getService(SettingsService.class);
    }

    @Override
    public @Nullable ApplicationSettings getState() {
        if (settings == null) {
            settings = new ApplicationSettings();
        }
        return settings;
    }

    @Override
    public void loadState(@NotNull ApplicationSettings state) {
        this.settings = state;
    }
}
