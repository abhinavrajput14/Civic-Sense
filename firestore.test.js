const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read levels", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("levels").get());
});

test("Authenticated user: can read levels", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("levels").get());
});

test("Authenticated user: cannot read another user's profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

test("Authenticated user: can read and write own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profileRef = aliceDb.collection("users").doc(ALICE_UID);
  await assertSucceeds(profileRef.set({
    userId: ALICE_UID,
    username: "Alice",
    coins: 100,
    xp: 50,
    civicPoints: 20,
    unlockedBadges: ["rookie"],
    createdAt: new Date(),
    updatedAt: new Date()
  }));
  await assertSucceeds(profileRef.get());
});

test("Authenticated user: can record own level progress", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const progressRef = aliceDb.collection("users").doc(ALICE_UID).collection("progress").doc("level_1");
  await assertSucceeds(progressRef.set({
    levelId: 1,
    stars: 3,
    firstTry: true,
    userId: ALICE_UID,
    completedAt: new Date()
  }));
});

test("Authenticated user: cannot record level progress for another user", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const progressRef = aliceDb.collection("users").doc(BOB_UID).collection("progress").doc("level_1");
  await assertFails(progressRef.set({
    levelId: 1,
    stars: 3,
    firstTry: true,
    userId: BOB_UID,
    completedAt: new Date()
  }));
});
