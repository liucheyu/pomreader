package idv.liucheyu.pomreader.service;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuService {
    public MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        return menuBar;
    }

    public MenuBar addMenuItem(MenuBar menuBar, int menuIndex, String itemName) {
        MenuItem menuItem = createMenuItem(itemName);
        menuBar.getMenus().get(menuIndex).getItems().add(menuItem);
        return menuBar;
    }

    public MenuItem createMenuItem(String itemName) {
        MenuItem menuItem = new MenuItem(itemName);
        return menuItem;
    }

    public Menu createMenu(String menuName) {
        Menu menu = new Menu(menuName);
        return menu;
    }
}
