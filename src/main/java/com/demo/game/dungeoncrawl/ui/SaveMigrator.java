package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.DoorData;
import com.demo.game.dungeoncrawl.dto.EnemyData;
import com.demo.game.dungeoncrawl.dto.ItemData;
import com.demo.game.dungeoncrawl.dto.SaveData;

import java.util.ArrayList;
import java.util.logging.Logger;

final class SaveMigrator {
    private static final Logger LOGGER = Logger.getLogger(SaveMigrator.class.getName());

    private SaveMigrator() {
    }

    static SaveData migrate(SaveData data) {
        if (data == null) {
            return null;
        }

        if (data.version > SaveData.CURRENT_VERSION) {
            LOGGER.warning("Save version " + data.version + " is newer than supported version "
                    + SaveData.CURRENT_VERSION + ".");
            return data;
        }

        if (data.version <= 0) {
            migrateLegacySave(data);
        }

        if (data.version < 2) {
            migrateV1ToV2(data);
        }

        data.version = SaveData.CURRENT_VERSION;
        return data;
    }

    private static void migrateLegacySave(SaveData data) {
        data.version = 1;
        ensureCollections(data);
    }

    private static void migrateV1ToV2(SaveData data) {
        data.version = 2;
        ensureCollections(data);
        // DoorData.requiredKey and DoorData.isExit are restored from the base map when absent.
    }

    private static void ensureCollections(SaveData data) {
        if (data.enemies == null) {
            data.enemies = new ArrayList<EnemyData>();
        }

        if (data.doors == null) {
            data.doors = new ArrayList<DoorData>();
        }

        if (data.items == null) {
            data.items = new ArrayList<ItemData>();
        }
    }
}
