package cho.info.simonSays;

import cho.info.passwords.api.PasswordsApi;
import cho.info.passwords.api.password.customgui.PasswordsGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SimonGui extends PasswordsGui {

    private final PasswordsApi passwordsApi;
    private final SimonSays simonSays;
    private final Inventory inventory;

    private final List<String> randomList;
    private final List<String> display = new ArrayList<>();

    private final Map<Integer, ItemStack> normalColors = new HashMap<>();
    private final Map<Integer, ItemStack> highlightColors = new HashMap<>();

    public SimonGui(PasswordsApi passwordsApi, SimonSays simonSays) {
        this.passwordsApi = passwordsApi;
        this.simonSays = simonSays;
        this.inventory = Bukkit.createInventory(null, 9, "Simon Says");
        this.randomList = getRandomColorList();

        initColorMaps();
    }

    private void initColorMaps() {
        Material[] woolColors = {
                Material.RED_WOOL, Material.GREEN_WOOL, Material.BLUE_WOOL,
                Material.YELLOW_WOOL, Material.ORANGE_WOOL, Material.PURPLE_WOOL,
                Material.LIME_WOOL, Material.PINK_WOOL, Material.CYAN_WOOL
        };

        Material[] glassColors = {
                Material.RED_STAINED_GLASS, Material.GREEN_STAINED_GLASS, Material.BLUE_STAINED_GLASS,
                Material.YELLOW_STAINED_GLASS, Material.ORANGE_STAINED_GLASS, Material.PURPLE_STAINED_GLASS,
                Material.LIME_STAINED_GLASS, Material.PINK_STAINED_GLASS, Material.CYAN_STAINED_GLASS
        };

        String[] names = {"Red", "Green", "Blue", "Yellow", "Orange", "Purple", "Lime", "Pink", "Cyan"};

        for (int i = 0; i < 9; i++) {
            normalColors.put(i, createItem(woolColors[i], names[i]));
            highlightColors.put(i, createItem(glassColors[i], names[i]));
        }
    }

    @Override
    public void openGui(PlayerJoinEvent playerJoinEvent) {
        passwordsApi.customGui().registerChar(playerJoinEvent.getPlayer());

        // Setze normale Farben ins Inventar
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, normalColors.get(i));
        }

        displayColor(randomList.get(0));

        playerJoinEvent.getPlayer().openInventory(inventory);
    }

    @Override
    public void interactGui(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getCurrentItem() == null || !inventoryClickEvent.getCurrentItem().hasItemMeta()) {
            return;
        }

        String displayName = inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName();
        Player player = (Player) inventoryClickEvent.getWhoClicked();

        if (displayName.equals(getColorName(randomList.get(0)))) {
            display.add(displayName);
            randomList.remove(0);

            if (display.size() == 9) {
                display.clear();
                passwordsApi.customGui().setLogin(true, player);
                player.closeInventory();
            } else {
                displayColorGui();
            }

        } else {
            display.clear();
            player.kickPlayer("Wrong color! Try again.");
        }
    }

    @Override
    public void closeGui(InventoryCloseEvent inventoryCloseEvent) {
        // Falls etwas beim Schließen gemacht werden muss
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }

    private List<String> getRandomColorList() {
        List<String> colors = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8");
        Collections.shuffle(colors);
        return colors;
    }

    private String getColorName(String number) {
        switch (number) {
            case "0": return "Red";
            case "1": return "Green";
            case "2": return "Blue";
            case "3": return "Yellow";
            case "4": return "Orange";
            case "5": return "Purple";
            case "6": return "Lime";
            case "7": return "Pink";
            case "8": return "Cyan";
            default: return "Red";
        }
    }

    private void displayColorGui() {
        if (!randomList.isEmpty()) {
            displayColor(randomList.get(0));
        }
    }

    private void displayColor(String number) {
        int index = Integer.parseInt(number);

        if (!normalColors.containsKey(index) || !highlightColors.containsKey(index)) {
            return;
        }

        inventory.setItem(index, highlightColors.get(index));

        Bukkit.getScheduler().runTaskLater(simonSays, () -> {
            inventory.setItem(index, normalColors.get(index));
        }, 60L);
    }
}
