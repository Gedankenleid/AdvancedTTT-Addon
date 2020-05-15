package org.sevennb.ttt;

import net.labymod.api.LabyModAddon;
import net.labymod.api.events.MessageReceiveEvent;
import net.labymod.api.events.MessageSendEvent;
import net.labymod.main.LabyMod;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.KeyElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import net.labymod.utils.ModColor;
import net.labymod.utils.ServerData;
import org.lwjgl.input.Keyboard;
import org.sevennb.ttt.events.NameTag;
import org.sevennb.ttt.modules.*;
import org.sevennb.ttt.utils.*;
import org.sevennb.ttt.utils.data.WebHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

public class TTTAddon extends LabyModAddon {

    public static boolean STATUS;
    public static HashMap<String, Integer> testlevel = new HashMap<String, Integer>();
    public static boolean NAMETAGS;
    public static boolean ACTION;
    public static final double VERSION = 1.8;
    public static boolean UPDATE = false;
    public static String ACTIONBAR = ModColor.cl('9') + "Advanced"+ModColor.cl('4')+ModColor.cl('l')+"TTT "+ModColor.cl('8')+ModColor.cl('l')+"↠ "+ModColor.cl("9")+ TTTAddon.VERSION;
    public static List<String> DEVELOPERS = new ArrayList<String>();
    public static double CURRENT;
    public static String LASTKILL = "";

    @Override
    public void onEnable() {
        this.getApi().getEventManager().registerOnJoin(new Consumer<ServerData>() {
            @Override
            public void accept(ServerData serverData) {
                CURRENT = Double.parseDouble(WebHandler.read("https://7nb.org/mod/version.txt"));
                String download = "https://7nb.org/mod/"+ CURRENT;
                if(VERSION != CURRENT){
                    LabyMod.getInstance().displayMessageInChat("§3Advanced§4§lTTT§7> §eEs ist eine §4neue Version §everfügbar!");
                    LabyMod.getInstance().displayMessageInChat("§3Advanced§4§lTTT§7> §eDu nutzt die Version: §4"+VERSION+"§e!");
                    LabyMod.getInstance().displayMessageInChat("§3Advanced§4§lTTT§7> §eLade dir die Version §4"+ CURRENT +" §ehier herunter:");
                    LabyMod.getInstance().displayMessageInChat("§3Advanced§4§lTTT§7> §3"+download);
                    UPDATE = true;
                }else{
                    LabyMod.getInstance().displayMessageInChat("§3Advanced§4§lTTT§7> §4Version: "+ CURRENT +" §eKeine Updates verfügbar.");
                }
                if(DEVELOPERS.contains(LabyMod.getInstance().getPlayerName())){
                    LabyMod.getInstance().displayMessageInChat("§3Advanced§4§lTTT§7> §eDu bist ein §4Administrator§e!");
                }
            }
        });

        this.getApi().getEventManager().register(new MessageReceiveEvent() {
            @Override
            public boolean onReceive(String s, String message) {
                if((message != null) || (message != "")){
                    try {
                        if(message.contains("Die Traitor-Falle wurde ausgelöst")){
                            FalleModule.falle = "§4§lJa";
                            return false;
                        }
                        MessageUtils.execute(message);
                    }catch (Exception e){System.out.println(TextColor.ANSI_RED+e+TextColor.ANSI_RESET);}
                }else{
                    System.out.println("Message ist null!");
                }
                return false;
            }
        });

        this.getApi().getEventManager().register(new MessageSendEvent() {
            @Override
            public boolean onSend(String message) {
                        if (TTTAddon.STATUS) {
                            if(MessageUtils.command(message)){
                                return true;
                            }else{
                                return false;
                            }
                        }
                        return false;
            }
        });

        this.getApi().getEventManager().registerOnJoin(new Consumer<ServerData>(){

            @Override
            public void accept(ServerData serverData) {
                testlevel.clear();
                ListUtils.tests.clear();
                System.out.println(TextColor.ANSI_RED+"Leere die Liste!"+TextColor.ANSI_RESET);
            }
        });

        this.getApi().registerModule(new ListModule());
        this.getApi().registerModule(new SecondModule());
        this.getApi().registerModule(new FalleModule());
        this.getApi().registerModule(new RolleModule());
        this.getApi().registerForgeListener(new NameTag());
        this.getApi().registerForgeListener(new RolleOverlay());
        Timer actionbartimer = new Timer();
        actionbartimer.schedule(new ActionbarManager(), 0, 1);
        System.out.println(TextColor.ANSI_GREEN+"TTTAddon aktiviert!"+TextColor.ANSI_RESET);
    }

    @Override
    public void onDisable() {
        System.out.println(TextColor.ANSI_RED+"TTTAddon deaktiviert!"+TextColor.ANSI_RESET);
    }

    @Override
    public void loadConfig() {
        this.STATUS = getConfig().has( "active" ) ? getConfig().get( "active" ).getAsBoolean() : true;
        this.NAMETAGS = getConfig().has( "nametags" ) ? getConfig().get( "nametags" ).getAsBoolean() : true;
        this.ACTION = getConfig().has( "action" ) ? getConfig().get( "action" ).getAsBoolean() : true;
    }

    @Override
    protected void fillSettings(List<SettingsElement> subSettings) {
        subSettings.add( new BooleanElement( "Aktiv", this, new ControlElement.IconData( Material.LEVER ), "active", this.STATUS ) );
        subSettings.add( new BooleanElement( "NameTags", this, new ControlElement.IconData( Material.LEVER ), "nametags", this.NAMETAGS ) );
        subSettings.add( new BooleanElement( "Actionbar", this, new ControlElement.IconData( Material.LEVER ), "action", this.NAMETAGS ) );
    }
}
