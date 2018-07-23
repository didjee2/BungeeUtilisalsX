package com.dbsoftwares.bungeeutilisals.manager;

import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.dbsoftwares.bungeeutilisals.api.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.bungeeutilisals.api.utils.unicode.UnicodeTranslator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ChatManager implements IChatManager {

    private Pattern ippattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
    private Pattern webpattern = Pattern.compile("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$");

    private List<Pattern> swearPatterns = Lists.newArrayList();
    private Map<String, String> utfSymbols = Maps.newHashMap();

    public void reloadPatterns() {
        swearPatterns.clear();

        for (String word : FileLocation.ANTISWEAR.getConfiguration().getStringList("words")) {
            StringBuilder builder = new StringBuilder("\\b(");

            for (char o : word.toCharArray()) {
                builder.append(o);
                builder.append("+(\\W|\\d\\_)*");
            }
            builder.append(")\\b");

            swearPatterns.add(Pattern.compile(builder.toString()));
        }

        ISection section = FileLocation.UTFSYMBOLS.getConfiguration().getSection("symbols.symbols");
        for (String key : section.getKeys()) {
            utfSymbols.put(key, section.getString(key));
        }
    }

    @Override
    public Boolean checkForAdvertisement(User user, String message) {
        IConfiguration config = FileLocation.ANTIAD.getConfiguration();
        if (!config.getBoolean("enabled") || user.getParent().hasPermission(config.getString("bypass"))) {
            return false;
        }
        message = message.replaceAll("[^A-Za-z0-9:/]", "");
        for (String word : message.split(" ")) {
            if (config.getStringList("allowed").contains(word.toLowerCase())) {
                continue;
            }
            if (ippattern.matcher(word).find() || webpattern.matcher(word).find()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean checkForCaps(User user, String message) {
        IConfiguration config = FileLocation.ANTICAPS.getConfiguration();

        if (!config.getBoolean("enabled") || user.getParent().hasPermission(config.getString("bypass"))
                || message.length() < config.getInteger("min-length")) {
            return false;
        }

        Double upperCase = 0.0D;
        for (int i = 0; i < message.length(); i++) {
            if (config.getString("characters").contains(message.substring(i, i + 1))) {
                upperCase += 1.0D;
            }
        }

        return (upperCase / message.length()) > ((config.getInteger("percentage")) / 100);
    }

    @Override
    public Boolean checkForSpam(User user) {
        IConfiguration config = FileLocation.ANTISPAM.getConfiguration();
        if (!config.getBoolean("enabled") || user.getParent().hasPermission(config.getString("bypass"))) {
            return false;
        }
        if (!user.getCooldowns().canUse("CHATSPAM")) {
            return true;
        }
        user.getCooldowns().updateTime("CHATSPAM",
                TimeUnit.valueOf(config.getString("delay.unit").toUpperCase()),
                config.getInteger("delay.time"));
        return false;
    }

    @Override
    public Boolean checkForSwear(User user, String message) {
        IConfiguration config = FileLocation.ANTISWEAR.getConfiguration();
        if (!config.getBoolean("enabled") || user.getParent().hasPermission(config.getString("bypass"))) {
            return false;
        }

        for (Pattern pattern : swearPatterns) {
            if (pattern.matcher(message).find()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String replaceSwearWords(User user, String message, String replacement) {
        IConfiguration config = FileLocation.ANTISWEAR.getConfiguration();
        if (!config.getBoolean("enabled") || user.getParent().hasPermission(config.getString("bypass"))) {
            return message;
        }

        for (Pattern pattern : swearPatterns) {
            if (pattern.matcher(message).find()) {
                message = pattern.matcher(message).replaceAll(replacement);
            }
        }
        return message;
    }

    @Override
    public String replaceSymbols(String message) {
        for (Map.Entry<String, String> entry : utfSymbols.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value.contains(", ")) {
                for (String val : value.split(", ")) {
                    message = message.replace(val, UnicodeTranslator.translate(key));
                }
            } else {
                message = message.replace(value, UnicodeTranslator.translate(key));
            }
        }
        return message;
    }

    @Override
    public String fancyFont(String message) {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,?;.:+=-%*$!'\"@|&<>0123456789#".toCharArray();
        StringBuilder builder = new StringBuilder();

        for (char replaceableChar : message.toCharArray()) {
            int i = 0;
            Boolean charFound = false;
            while (!charFound && i < chars.length) {
                if (chars[i] == replaceableChar) {
                    charFound = true;
                }
                i++;
            }
            if (charFound) {
                builder.append((char) (65248 + replaceableChar));
            } else {
                builder.append(replaceableChar);
            }
        }

        return builder.toString();
    }
}