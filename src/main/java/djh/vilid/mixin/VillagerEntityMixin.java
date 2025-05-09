package djh.vilid.mixin;

import djh.vilid.item.custom.*;
import djh.vilid.pol.Ideology;
import djh.vilid.villager.VillagerExt;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

//import static djh.vilid.villager.ModActivities.createLearnTasks;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin implements VillagerExt{


	//
//	@Inject(at = @At("HEAD"), method = "initBrain(Lnet/minecraft/entity/ai/brain/Brain;)V", locals = LocalCapture.CAPTURE_FAILEXCEPTION)
//	private void initBrain(Brain<VillagerEntity> brain, CallbackInfo info) {
//		VillagerEntity $this = (VillagerEntity) (Object) this;
//
//		if (!$this.isBaby()) {
//			brain.setTaskList(
//					ModActivities.LEARN,
//
//					createLearnTasks()
//			);
//		}
//	}

	@Shadow protected abstract void sayNo();

	//influence ideology based on new profession
	@Inject(method = "setVillagerData", at = @At("HEAD"))
	private void beforeVillagerDataChanged(VillagerData data, CallbackInfo ci) {
//		VillagerEntity villager = (VillagerEntity) (Object) this;
//		VillagerExt ext = (VillagerExt) villager;
//		VillagerData oldData = villager.getVillagerData();
//
//		if (!oldData.getProfession().equals(data.getProfession())){
//			//reset villager's ideology to their OG beliefs
//			ext.getViewpoint().resetIdeology();
//			//attempt to influence villager's politics by their new job's leanings
////			ext.getViewpoint().attemptInfluence(Ideology.jobWeights.get(data.getProfession()));
//		}
	}

	//allow right click with propaganda items
	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void cancelTradeIfInfluencing(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		ItemStack stack = player.getStackInHand(hand);
		VillagerEntity villager = (VillagerEntity) (Object) this;

		if (stack.getItem() instanceof BadItem || stack.getItem() instanceof EducatorItem || stack.getItem() instanceof DumbItem || stack.getItem() instanceof GoodItem || stack.getItem() instanceof LeftItem || stack.getItem() instanceof RightItem) {
			// Cancel default trade GUI
			stack.useOnEntity(player,villager,hand);
			cir.setReturnValue(ActionResult.SUCCESS);
		}
	}

	//allow right click with polling item
	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void cancelTradeIfPolling(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		ItemStack stack = player.getStackInHand(hand);
		VillagerEntity villager = (VillagerEntity) (Object) this;

		if (stack.getItem() instanceof PollerItem) {
			// Cancel default trade GUI
			stack.useOnEntity(player,villager,hand);
			cir.setReturnValue(ActionResult.SUCCESS);
		}
	}

	//low happiness means no trades
	@Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
	private void cancelTradeIfRadical(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
//		VillagerEntity villager = (VillagerEntity) (Object) this;
//		VillagerExt ext = (VillagerExt) villager;

		if (!player.getWorld().isClient()) {
			if (getIdeology().happinessUnder(50)) {
				sayNo();
				player.sendMessage(Text.literal(getLegalName()+" is too unhappy to trade!"));

				cir.cancel();
			}
		}
	}

	//become a pillager
	public void Criminalize(){
		VillagerEntity villager = (VillagerEntity) (Object) this;
		VillagerExt ext = (VillagerExt) villager;

		//summon criminal at villager location
		VindicatorEntity vindicator = new VindicatorEntity(EntityType.VINDICATOR, villager.getWorld());
		vindicator.refreshPositionAndAngles(
				villager.getX(),
				villager.getY(),
				villager.getZ(),
				villager.getYaw(),
				0f
		);

		//give it villager's old name
		String name = ext.getLegalName();
		vindicator.setCustomName(Text.literal(name));
		vindicator.setCustomNameVisible(true);

		//arm him
		vindicator.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
		vindicator.setCurrentHand(Hand.MAIN_HAND);

		//replace villager
		villager.getWorld().spawnEntity(vindicator);
		villager.discard();
	}

	//nbt storage vars
	private final String legalNameTag = "legalName";
	private String legalName;
//	private final String viewpointTag = "viewpoint";
//	private Viewpoint viewpoint;
	private final String ideologyTag = "ideology";
	private Ideology ideology;

	//nbt initial var generation
	//default startup storage method

	public void genBaseNBT(VillagerProfession prof){
		if (getLegalName()==null){
			String[] names = {"George","Terrance","Charlie","Paddy","Joseph","Mark","Mary","Harold","Sue","Jane","Helen","Janice","Josh","Donny","Barry","Melissa","Rose","Drake","Hillary","Lori","Porky","Jim","Mike","Richard","Wilson","Chris","Christina","Sarah","Mitch","Mr. Z","Levi","Victoria","Blemish"};
			char[] lInitials = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
			setLegalName(names[new Random().nextInt(names.length)]+", "+ lInitials[new Random().nextInt(lInitials.length)] +".");
		}
		if (getIdeology()==null){
			Ideology i = new Ideology();
			i.randomize();
			setIdeology(i);
		}
	}


	//nbt storage methods
	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		nbt.putString(legalNameTag, legalName);
		nbt.put(ideologyTag, ideology.toNbt());
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		if (nbt.contains(legalNameTag)) {
			legalName = nbt.getString(legalNameTag);
		}
		if (nbt.contains(ideologyTag)) {
			setIdeology(Ideology.fromNbt(nbt.getCompound(ideologyTag)));
		}
	}

	public String getLegalName() {
		return legalName;
	}
	public void setLegalName(String value) {
		legalName = value;
	}

	public Ideology getIdeology() {
		return ideology;
	}
	public void setIdeology(Ideology i) {
		ideology = i;
	}


	//socialization and life satisfaction stuff
	private boolean hasMetToday = false;

	@Inject(method = "tick", at = @At("TAIL"))
	private void onTick(CallbackInfo ci) {
		VillagerEntity self = (VillagerEntity) (Object) this;
		// Check if the villager’s brain is in MEET activity (socializing)
		if (!hasMetToday() && (self.getBrain().hasActivity(Activity.MEET) || self.getBrain().hasActivity(Activity.MEET))) {
			this.markMetToday();
		}
	}

	public void markMetToday() {
		this.hasMetToday = true;
	}

	public boolean hasMetToday() {
		return this.hasMetToday;
	}

	public void resetMetToday() {
		this.hasMetToday = false;
	}
}