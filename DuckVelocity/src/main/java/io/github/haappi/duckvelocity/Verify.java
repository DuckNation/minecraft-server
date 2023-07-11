package io.github.haappi.duckvelocity;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Verify implements RawCommand {
    private final MiniMessage mm = DuckVelocity.getInstance().getMiniMessage();

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            return;
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("uuid", player.getUniqueId().toString());
        params.put("username", player.getUsername());
        HttpPost req = new HttpPost(Config.API_BASE_URL + "/verification/create-pin?" + Utils.createQueryString(params));

        List<Object> resp = Utils.performHttpRequest(req);

        Integer statusCode = (Integer) resp.get(0);
        JSONObject body = (JSONObject) resp.get(1);

        if (statusCode != 200) {
            player.sendMessage(mm.deserialize(body.getString("detail")));
            return;
        }

        player.sendMessage(mm.deserialize(body.getString("message")));
    }

}
