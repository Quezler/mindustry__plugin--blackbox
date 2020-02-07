package blackbox;

import arc.*;
import arc.files.*;
import arc.util.*;
import mindustry.core.GameState.*;
import mindustry.game.EventType.*;
import mindustry.io.*;
import mindustry.plugin.*;

import static arc.util.Log.*;
import static mindustry.Vars.*;

public class BlackboxMod extends Plugin{

    private Interval timer = new Interval();
    private int minutes;

    @Override
    public void init(){
        minutes = Core.settings.getInt("autosave", 1);

        Events.on(Trigger.update, () -> {
            if(timer.get(60 * 60 * minutes)){
                if(state.is(State.playing)){
                    Log.info("&lm[autosave]");
                    Fi file = saveDirectory.child("autosave" + "." + saveExtension);

                    Core.app.post(() -> SaveIO.save(file));
                }
            }
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        handler.register("autosave", "[off/somenumber]", "Autosave every # minutes.", arg -> {
            if(arg.length == 0){
                info("Autosave interval is currently &lc{0}.", minutes);
                return;
            }

            if(arg[0].equals("off")){
                netServer.admins.setPlayerLimit(0);
                info("Autosave disabled.");
                return;
            }

            if(Strings.canParsePostiveInt(arg[0]) && Strings.parseInt(arg[0]) > 0){
                minutes = Strings.parseInt(arg[0]);
                Core.settings.putSave("autosave", minutes);
                info("Autosave interval is now &lc{0}.", minutes);
            }else{
                err("Autosave interval must be a number above 0.");
            }
        });

    }
}
