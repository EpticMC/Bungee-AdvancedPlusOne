package org.nulldev.AdvancedPlusOne;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
 
public class Main extends Plugin implements Listener {
    public static Main instance;
    public static File configFile;
    public static int MaxSlots;
    public static boolean useProt;
    public String protName;
    private ServerPing.Protocol version;
    public static ConfigurationProvider configurationProvider;
    
    static {
        configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    }
	
  @Override
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
      this.getProxy().getPluginManager().registerListener(this, this);
   }
	
  public void createConfig() throws IOException {
	  String yhFinal = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	  String defaultval = "My Cool Server";
      File folder = new File(this.getDataFolder().getPath());
      if (!folder.exists()) {
          folder.mkdir();
      }
      if (!(Main.configFile = new File(this.getDataFolder(), "config.yml")).exists()) {
          configFile.createNewFile();
      }
      //weird way to add comments (need to find out how)
      Configuration config = configurationProvider.load(configFile);
      //this.addDefault(config, null, "# This is the Player Limit on when the Plugin starts counting +1");
      this.addDefault(config, "MaxSlots", yhFinal);
      //this.addDefault(config, null, "# Should a custom server version be displayed? (For example \"MyCoolServer\" instead of \"Bungee X.X.X\")");
      this.addDefault(config, "EnableCustomProtocol", true);
      //this.addDefault(config, null, "# Set the custom server version here. (The option above must be true!");
      this.addDefault(config, "ProtocolName", defaultval);
      configurationProvider.save(config, configFile);
  }
  private void addDefault(Configuration config, String path, Object value) {
      if (!config.contains(path)) {
          config.set(path, value);
      }
  }
  
  public void setProt(ServerPing response) {
      if (response != null) {
          this.version = response.getVersion();
          this.version.setName(this.protName);
          this.version.setProtocol(999);
          response.setVersion(this.version);
      }
  }
  
  public void loadConfig() throws IOException {
      Configuration config = configurationProvider.load(configFile);
      MaxSlots = config.getInt("MaxSlots");
      useProt = config.getBoolean("EnableCustomProtocol");
      protName = config.getString("ProtocolName");
  }
  
  @EventHandler
  public void onPing(ProxyPingEvent ev) {
		ServerPing r = ev.getResponse();
		Players p = r.getPlayers();
	        if (useProt == true){
                        this.setProt(ev.getResponse());
	        }
	  //For some reason this is always true
		if(p.getOnline() >= MaxSlots){
			p = new Players(p.getOnline() + 1, p.getOnline(), p.getSample());
			@SuppressWarnings("deprecation")
			ServerPing ping = new ServerPing(r.getVersion(), p, r.getDescription(), r.getFaviconObject());
			ev.setResponse(ping);
		}
	  	//This is never ture, I don't know why yet. (Maby MaxSlots is NULL?)
		else if (p.getOnline() < MaxSlots){
			p = new Players(MaxSlots, p.getOnline(), p.getSample());
			p.setMax(MaxSlots);
			@SuppressWarnings("deprecation")
			ServerPing ping = new ServerPing(r.getVersion(), p, r.getDescription(), r.getFaviconObject());
			ev.setResponse(ping);
		}
	}
}
