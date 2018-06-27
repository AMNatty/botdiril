package cz.tefek.botdiril.userdata.payment;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;

import cz.tefek.botdiril.userdata.UserStorage;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

//A large todo
@SuppressWarnings("unused")
public class Gateway
{
    public static final int OK = 0x00;
    public static final int ERROR_BAD_BASE64 = 0x01;
    public static final int ERROR_BAD_JSON = 0x02;
    public static final int ERROR_NO_SUCH_USER = 0x03;
    public static final int ERROR_NO_USER_DATA = 0x04;
    public static final int ERROR_NO_SUCH_GUILD = 0x05;
    public static final int ERROR_NO_SUCH_CHANNEL = 0x06;
    public static final int ERROR_NEGATIVE_COST = 0x07;
    public static final int ERROR_NOT_ENOUGH_FUNDS = 0x08;
    public static final int ERROR_GUILD_NOT_MUTUAL = 0x09;

    private static final Decoder decoder = Base64.getDecoder();
    private static final Encoder encoder = Base64.getEncoder();

    public static void processPayment(PrivateChannel channel, String message, User requester)
    {
        if (true)
        {
            // Let's disable it for now
            return;
        }

        final var jda = channel.getJDA();

        try
        {
            var json = new String(decoder.decode(message));

            var tok = new JSONTokener(json);

            var jobj = new JSONObject(tok);

            var uid = jobj.getLong("id");

            var user = jda.getUserById(uid);

            var guild = jda.getGuildById(jobj.getLong("guild"));

            var key = jobj.getString("unique-key");

            if (guild == null)
            {
                sendError(channel, ERROR_NO_SUCH_GUILD, key, "No such guild.");

                return;
            }

            var rchannel = guild.getTextChannelById(jobj.getLong("text-channel"));

            if (rchannel == null)
            {
                sendError(channel, ERROR_NO_SUCH_CHANNEL, key, "No such text channel.");

                return;
            }

            if (user != null)
            {
                var ui = UserStorage.getByID(uid);

                if (ui != null)
                {
                    var cost = jobj.getLong("amount");

                    if (cost < 0)
                    {
                        sendError(channel, ERROR_NEGATIVE_COST, key, "Purchase cost cannot be negative. Selling items is not supported.");
                        return;
                    }

                    // Quick fail for better UX. however when the user confirms the payment we must
                    // check once more in case he wasted money somewhere else.
                    if (ui.getCoins() < cost)
                    {
                        sendError(channel, ERROR_NOT_ENOUGH_FUNDS, key, "The user doesn't have enough coins for this item.");
                        return;
                    }

                    PayRequest.addPayRequest(cost, user, rchannel, jobj.optString("item", "unspecified"), System.currentTimeMillis(), key, channel);
                }
                else
                {
                    sendError(channel, ERROR_NO_USER_DATA, key, "No user data for this ID.");
                    return;
                }
            }
            else
            {
                sendError(channel, ERROR_NO_SUCH_USER, key, "No such user.");
                return;
            }
        }
        catch (JSONException e)
        {
            sendError(channel, ERROR_BAD_JSON, "Malformed input JSON or there are some missing keys. Required keys: id[long], unique-key[string], guild[long], text-channel[long], amount[long]. Optional keys: item[string]");
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            sendError(channel, ERROR_BAD_BASE64, "Illegal Base64 encoding scheme.");
        }
    }

    public static void sendError(PrivateChannel pm, int code, String reason)
    {
        var out = new StringBuilder();
        var jw = new JSONWriter(out);
        jw.object().key("status").value("error").key("code").value(code).key("reason").value(reason).endObject();
        pm.sendMessage("Response:" + encoder.encodeToString(out.toString().getBytes())).submit();
    }

    public static void sendError(PrivateChannel pm, int code, String uniqueKey, String reason)
    {
        var out = new StringBuilder();
        var jw = new JSONWriter(out);
        jw.object().key("status").value("error").key("unique-key").value(uniqueKey).key("code").value(code).key("reason").value(reason).endObject();
        pm.sendMessage("Response:" + encoder.encodeToString(out.toString().getBytes())).submit();
    }

    public static void sendOK(PrivateChannel pm, String uniqueKey)
    {
        var out = new StringBuilder();
        var jw = new JSONWriter(out);
        jw.object().key("status").value("ok").key("code").value(OK).key("unique-key").value(uniqueKey).key("reason").value("Botdiril is now processing the payment.").endObject();
        pm.sendMessage("Response:" + encoder.encodeToString(out.toString().getBytes())).submit();
    }
}
