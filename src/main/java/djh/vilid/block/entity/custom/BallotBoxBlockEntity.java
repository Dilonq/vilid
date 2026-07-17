//package djh.vilid.block.entity.custom;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.BlockEntityType;
//import net.minecraft.block.entity.LootableContainerBlockEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.inventory.Inventories;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.screen.GenericContainerScreenHandler;
//import net.minecraft.screen.ScreenHandler;
//import net.minecraft.text.Text;
//import net.minecraft.util.collection.DefaultedList;
//import net.minecraft.util.math.BlockPos;
//
//public class BallotBoxBlockEntity  extends LootableContainerBlockEntity {
//
//    public BallotBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
//        super(type, pos, state);
//    }
//
//    @Override
//    protected DefaultedList<ItemStack> getInvStackList() {
//        return null;
//    }
//
//    @Override
//    protected void setInvStackList(DefaultedList<ItemStack> list) {
//
//    }
//
//    @Override
//    protected Text getContainerName() {
//        return Text.translatable("container.vilid.ballot_box");
//    }
//
//    @Override
//    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
//        return null;
//    }
//
//
////    @Override
////    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
////        // Leverages vanilla's 3-row chest GUI container directly
////        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
////    }
////
//
//    // Save inventory data to NBT when chunk unloads/saves
//    @Override
//    public void writeNbt(NbtCompound nbt) {
//        super.writeNbt(nbt);
////        if (!this.serializeLootTable(nbt)) {
////            Inventories.writeNbt(nbt, this.inventory);
////        }
//    }
//
//    // Read inventory back from NBT when chunk loads
//    @Override
//    public void readNbt(NbtCompound nbt) {
//        super.readNbt(nbt);
////        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
////        if (!this.deserializeLootTable(nbt)) {
////            Inventories.readNbt(nbt, this.inventory);
////        }
//    }
//
//    @Override
//    public int size() {
//        return 0;
//    }
//}