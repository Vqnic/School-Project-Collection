const functions = require("firebase-functions");
const admin = require("firebase-admin");
const {FieldValue} = require("firebase-admin/firestore");
const z = require("zod"); //Import zod library for validation
admin.initializeApp();

const cors = require("cors")({origin: true});

function getShoppingData() {
  if(admin.apps.length === 0) {
    admin.initializeApp();
  }
  const shoppingdata = admin.firestore().collection("mylist");
  return shoppingdata;
}

exports.validateItem = functions.https.onRequest((req, res) => {
  if (req.method !== "POST") {
    res.status(405).json({error: "Method not allowed"});
    return;
  }

  const itemSchema = z.object({
    content: z.string({
      required_error: "Special Character Failure",
      invalid_type_error: "Special Character Failure"
    })
      .min(3, "Too short, minimum 3 characters")
      .regex(/^[A-Za-z ]+$/, "Special Character Failure"),
    style: z.enum(["cool", "hot", "complete"], {
      errorMap: () => ({message: "Invalid Style Failure"})
    })
  });

  const parsed = itemSchema.safeParse(req.body || {});
  if (!parsed.success) {
    const issueMessage = parsed.error.issues[0]?.message || "Special Character Failure";
    res.status(400).json({error: issueMessage});
    return;
  }
  res.status(200).json({message: "Success Test", data: parsed.data});
});


exports.clearList = functions.https.onRequest((request, response) => {
  cors(request, response, async () => {
    try {
      const shoppingdata = getShoppingData();
      const completedSnapshot = await shoppingdata.where("style", "==", "complete").get();

      if (completedSnapshot.empty) {
        console.log("No completed tasks to clear.");
        response.status(200).json({ok: true, deleted: 0});
        return;
      }

      const batch = admin.firestore().batch();
      completedSnapshot.forEach((docSnap) => {
        batch.delete(docSnap.ref);
      });
      await batch.commit();

      console.log(`Cleared ${completedSnapshot.size} completed tasks!`);
      response.status(200).json({ok: true, deleted: completedSnapshot.size});
    } catch (error) {
      console.error("Error clearing list:", error);
      response.status(500).json({error: "Error clearing list..."});
      return;
    }
  });
});

exports.changeStyle = functions.https.onRequest((request, response) => {
  cors(request, response, async () => {
    try {
      if (request.method === "OPTIONS") {
        response.status(204).send("");
        return;
      }
      if (request.method !== "POST") {
        response.status(405).json({error: "Method not allowed"});
        return;
      }
      
      const {id} = request.body;
      if (!id) {
        response.status(400).json({error: "Missing task id"});
        return;
      }

      const itemRef = getShoppingData().doc(id.toString());
      const itemSnapshot = await itemRef.get();
      if (!itemSnapshot.exists) {
        response.status(404).json({error: "Task not found"});
        return;
      }

      const currentStyle = itemSnapshot.data().style || "cool";
      let nextStyle;
      switch(currentStyle) {
        case "cool":
          nextStyle = "complete";
          break;
        case "complete":
          nextStyle = "hot";
          break;
        default: //hot or anything else defaults to cool
          nextStyle = "cool";
      };
      await itemRef.update({style: nextStyle});

      console.log(`Changed style for ${id} from ${currentStyle} to ${nextStyle}`);
      response.status(200).json({ok: true, id, style: nextStyle});
    } catch (error) {
      console.error("Error changing style:", error);
      response.status(500).json({error: "Error changing style..."});
      return;
    }
  });
});

exports.addItem = functions.https.onRequest((request, response) => {
  cors(request, response, async () => {
    if (request.method === "OPTIONS") {
      response.status(204).send("");
      return;
    }

    if (request.method !== "POST") {
      response.status(405).json({error: "Method not allowed"});
      return;
    }

    const validateLocally = (payload) => {
      return new Promise((resolve) => {
        const mockReq = {method: "POST", body: payload};
        const mockRes = {
          statusCode: 200,
          status(code) {
            this.statusCode = code;
            return this;
          },
          json(body) {
            resolve({statusCode: this.statusCode || 200, body});
          }
        };

        exports.validateItem(mockReq, mockRes);
      });
    };

    const validationResult = await validateLocally({
      content: request.body.content,
      style: request.body.style
    });

    if (validationResult.statusCode !== 200) {
      response.status(validationResult.statusCode).json(validationResult.body);
      return;
    }

    const {content, style} = validationResult.body.data;
    const createdBy = request.body.createdBy || "me";
    console.log("Content: ", content);
    console.log("Style: ", style);
    console.log("Server Time: ", FieldValue.serverTimestamp());

    const shoppingdata = getShoppingData();
    const newdocref = shoppingdata.doc();

    var newitem={
      id: newdocref.id,
      content: content,
      style: style,
      createdAt: FieldValue.serverTimestamp(),
      createdBy: createdBy
    };

    try {
      await newdocref.set(newitem);
      response.status(200).json({ok: true, id: newdocref.id});
    } catch (error) {
      console.error("Error adding item:", error);
      response.status(500).json({error: "Error adding item."});
      return;
    }
  });
});