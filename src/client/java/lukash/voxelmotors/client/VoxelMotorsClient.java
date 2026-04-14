package lukash.voxelmotors.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lukash.voxelmotors.VoxelMotors;


public class VoxelMotorsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(VoxelMotors.MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client initialized!");
    }
}
