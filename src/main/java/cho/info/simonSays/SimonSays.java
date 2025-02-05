package cho.info.simonSays;

import cho.info.passwords.Passwords;
import cho.info.passwords.api.PasswordsApi;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimonSays extends JavaPlugin {

    @Override
    public void onEnable() {
        Passwords passwords = new Passwords();
        PasswordsApi passwordsApi = passwords.getPasswordsApi();
        passwordsApi.customGui().setType("custom");

        SimonGui simonGui = new SimonGui(passwordsApi, this);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
