package com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.Mapping;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/*
 * Created by DBSoftwares on 15 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class MongoPunishmentDao implements PunishmentDao {

    @Override
    public long getPunishmentsSince(String identifier, PunishmentType type, Date date) {
        MongoCollection<Document> collection = getDatabase().getCollection(format(type.getTablePlaceHolder()));

        Bson idFilter = Filters.eq(type.isIP() ? "ip" : "uuid", identifier);
        Bson dateFilter = Filters.gt("date", date);

        return collection.countDocuments(Filters.and(idFilter, dateFilter));
    }

    @Override
    public PunishmentInfo insertPunishment(PunishmentType type, UUID uuid, String user,
                                           String ip, String reason, Long time, String server,
                                           Boolean active, String executedby) {
        PunishmentInfo info = new PunishmentInfo();

        info.setUuid(uuid);
        info.setUser(user);
        info.setIP(ip);
        info.setReason(reason);
        info.setServer(server);
        info.setExecutedBy(executedby);
        info.setDate(new Date(System.currentTimeMillis()));
        info.setType(type);

        Mapping<String, Object> mapping = new Mapping<>(true);
        mapping.append("uuid", uuid).append("user", user).append("ip", ip)
                .append("reason", reason).append("server", server)
                .append("date", new Date(System.currentTimeMillis()));
        if (time != null) {
            info.setExpireTime(time);
            mapping.append("time", time);
        }
        if (active != null) {
            info.setActive(active);
            mapping.append("active", active);
        }
        mapping.append("executed_by", executedby);

        getDatabase().getCollection(format(type.getTablePlaceHolder())).insertOne(new Document(mapping.getMap()));
        return info;
    }

    @Override
    public boolean isPunishmentPresent(PunishmentType type, UUID uuid, String IP, boolean checkActive) {
        List<Bson> filters = Lists.newArrayList();

        Validate.ifNotNull(uuid, u -> filters.add(Filters.eq("uuid", u)));
        Validate.ifNotNull(IP, ip -> filters.add(Filters.eq("ip", ip)));
        Validate.ifTrue(checkActive, active -> filters.add(Filters.eq("active", true)));

        if (filters.size() > 1) {
            return getDatabase().getCollection(format(type.getTablePlaceHolder()))
                    .find(Filters.and(filters)).iterator().hasNext();
        } else {
            return filters.size() == 1 &&
                    getDatabase().getCollection(format(type.getTablePlaceHolder()))
                            .find(filters.get(0)).iterator().hasNext();
        }
    }

    @Override
    public PunishmentInfo getPunishment(PunishmentType type, UUID uuid, String IP) {
        MongoCollection<Document> collection = getDatabase().getCollection(format(type.getTablePlaceHolder()));
        Document document = null;

        if (uuid != null) {
            document = collection.find(Filters.eq("uuid", uuid.toString())).first();
        } else if (IP != null) {
            document = collection.find(Filters.eq("ip", IP)).first();
        }

        if (document != null) {
            PunishmentInfo.PunishmentInfoBuilder builder = PunishmentInfo.builder();
            builder.uuid(uuid).user(document.getString("user")).IP(document.getString("ip"))
                    .reason(document.getString("reason")).server(document.getString("server"))
                    .date(document.getDate("date")).executedBy(document.getString("executed_by"))
                    .removedBy(document.containsKey("removed_by") ? null : document.getString("removed_by"))
                    .type(type);

            if (document.containsKey("active")) {
                builder.active(document.getBoolean("active"));
            }
            if (document.containsKey("time")) {
                builder.expireTime(document.getLong("time"));
            }

            return builder.build();
        }

        return PunishmentInfo.builder().build();
    }

    @Override
    public void removePunishment(PunishmentType type, UUID uuid, String ip) {
        MongoCollection<Document> coll = getDatabase().getCollection(format(type.getTablePlaceHolder()));

        if (uuid != null) {
            coll.updateOne(Filters.eq("uuid", uuid), Updates.set("active", false));
        } else if (ip != null) {
            coll.updateOne(Filters.eq("ip", ip), Updates.set("active", false));
        }
    }

    private String format(String line) {
        return PlaceHolderAPI.formatMessage(line);
    }

    private String format(String line, Object... replacements) {
        return PlaceHolderAPI.formatMessage(String.format(line, replacements));
    }

    private MongoDatabase getDatabase() {
        return ((MongoDBStorageManager) BungeeUtilisals.getInstance().getDatabaseManagement()).getDatabase();
    }
}