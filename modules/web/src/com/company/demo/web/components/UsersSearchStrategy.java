package com.company.demo.web.components;

import com.haulmont.addon.search.context.SearchContext;
import com.haulmont.addon.search.strategy.DefaultSearchEntry;
import com.haulmont.addon.search.strategy.SearchEntry;
import com.haulmont.addon.search.strategy.SearchStrategy;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.UuidProvider;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.web.AppUI;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component("search_UsersSearchStrategy")
public class UsersSearchStrategy implements SearchStrategy {
    @Nonnull
    @Override
    public String name() {
        return "usersSearchStrategy";
    }

    @Inject
    protected DataManager dataManager;

    @Nonnull
    @Override
    public List<SearchEntry> load(@Nonnull SearchContext context, String query) {
        LoadContext<User> lc = LoadContext.create(User.class);
        lc.setQueryString("select u from sec$User u where u.loginLowerCase like concat('%',:loginLowerCase,'%')")
                .setParameter("loginLowerCase", query.toLowerCase());

        return dataManager.loadList(lc).stream()
                .map(user -> new DefaultSearchEntry(user.getId().toString(), user.getCaption(), name()))
                .collect(Collectors.toList());
    }

    @Override
    public void invoke(@Nonnull SearchContext context, SearchEntry value) {
        LoadContext<User> selectedUser = LoadContext.create(User.class)
                .setId(UuidProvider.fromString(value.getId()));
        User user = dataManager.load(selectedUser);
        AppUI.getCurrent().getTopLevelWindow().openEditor(user, WindowManager.OpenType.NEW_TAB);
    }
}
