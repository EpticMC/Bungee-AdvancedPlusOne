package org.nulldev.AdvancedPlusOne;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class Main extends Plugin implements Listener {
    public static Main instance;
    public static File configFile;
    public static int MaxSlots;
    public static boolean useProt;
    public String protName;
    private ServerPing.Protocol version;
    public static ConfigurationProvider configurationProvider;

    static { configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class); }

    public void onEnable() {
        instance = this;
        try {
            this.createConfig();
            this.loadConfig();
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
        this.getProxy().getPluginManager().registerListener((Plugin)this, (Listener)this);
    }

    public void createConfig() throws IOException {
        String yhFinal = String.valueOf(Calendar.getInstance().get(1));
        String defaultval = "My Cool Server";
        File folder = new File(this.getDataFolder().getPath());
        if (!folder.exists()) folder.mkdir();
        if (!(Main.configFile = new File(this.getDataFolder(), "config.yml")).exists()) configFile.createNewFile();
        Configuration config = configurationProvider.load(configFile);
        this.addDefault(config, "MaxSlots", yhFinal);
        this.addDefault(config, "EnableCustomProtocol", true);
        this.addDefault(config, "ProtocolName", defaultval);
        configurationProvider.save(config, configFile);
    }

    private void addDefault(Configuration config, String path, Object value) { if (!config.contains(path)) config.set(path, value); }

    public void setProt(ServerPing response) {
        if (response != null) {
            this.version = response.getVersion();
            String max = Integer.toString(response.getPlayers().getMax());
            String online = Integer.toString(response.getPlayers().getOnline());
            if (online == max) this.version.setName((Object)ChatColor.GRAY + online + "/" + max + 1);
            else this.version.setName((Object)ChatColor.GRAY + online + "/" + max);
            this.version.setProtocol(999);
            response.setVersion(this.version);
        }
    }

    public void loadConfig() throws IOException {
        Configuration config = configurationProvider.load(configFile);
        MaxSlots = config.getInt("MaxSlots");
        useProt = config.getBoolean("EnableCustomProtocol");
        this.protName = config.getString("ProtocolName");
    }

    @EventHandler
    public void onPing(ProxyPingEvent ev) {
        ServerPing r = ev.getResponse();
        ServerPing.Players p = r.getPlayers();
        if (useProt) this.setProt(ev.getResponse());
        if (p.getOnline() >= MaxSlots) {
            p = new ServerPing.Players(MaxSlots + 1, p.getOnline(), p.getSample());
            p.setMax(MaxSlots + 1);
            ServerPing ping = new ServerPing(r.getVersion(), p, r.getDescription(), r.getFaviconObject());
            ev.setResponse(ping);
        } 
        else if (p.getOnline() < MaxSlots) {
            p = new ServerPing.Players(MaxSlots + 1, p.getOnline(), p.getSample());
            p.setMax(MaxSlots);
            ServerPing ping = new ServerPing(r.getVersion(), p, r.getDescription(), r.getFaviconObject());
            ev.setResponse(ping);
        }
    }
}

