const express = require('express');
const cors = require("cors");
const path = require('path');
const database = require('./database/connection');
const readDB = require('./database/read')
const insertDB = require('./database/create')
const updateDB = require('./database/update')
const deleteDB = require('./database/delete')
const CryptoJS = require('crypto-js');
const app = express();
const bodyParser = require('body-parser');
const multer = require('multer');
const { log } = require('console');

const PORT = 3000

// Carpeta imagenes
const publicDirPosts = path.join(__dirname, 'uploads', 'images');
const publicDirUsers = path.join(__dirname, 'uploads', 'user_img');
const publicDirVideos = path.join(__dirname, 'uploads', 'videos');

// Ruta de la carpeta
app.use('/uploads/images', express.static(publicDirPosts));
app.use('/uploads/user_img', express.static(publicDirUsers));
app.use('/uploads/videos', express.static(publicDirVideos));

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.json());
app.use(cors({
    credentials: true,
    origin: function (origin, callback) {
        return callback(null, true)
    }
}));

/* INSERT USUARI */
app.post('/addNewUser', (req, res) => {


    var urlImagen = 'http://localhost:3000/uploads/user_img/default_user_img.png';
    if (req.body.url_img) {
        urlImagen = req.body.url_img;
    }

    const usuari = {
        email: req.body.email,
        username: req.body.username,
        password: CryptoJS.SHA256(req.body.passwd).toString(),
        url_img: urlImagen,
        nombre: req.body.name,
        apellidos: req.body.surname,
        fecha_nac: req.body.date,
        followers: [],
        followings: []
    }

    /*const usuari = {
        email: "1email@1gmail.com",
        username: 'A19Narcis',
        password: CryptoJS.SHA256("Ausias_2003").toString(),
        url_img: 'http://localhost:3000/uploads/user_img/default_user_img.png',
        nombre: 'Narcis',
        apellidos: 'Gomez Carretero',
        fecha_nac: '28/08/2003'
    }*/

    insertDB.insertUsuari(usuari, function (resultInsert) {
        res.send(resultInsert);
    })
})


app.post('/checkUserExists', (req, res) => {
    const username = req.body.username

    readDB.checkUserExists(username, (exists) => {
        res.send(exists)
    })
})

app.post('/checkEmailExists', (req, res) => {
    const email = req.body.email

    readDB.checkEmailExists(email, (exists) => {
        res.send(exists)
    })
})

app.post('/loginGoogleAccount', (req, res) => {
    const email = req.body.email
    readDB.getUser(email, (dadesUser) => {
        var dadesFullUser = {
            _id: dadesUser._id,
            email: dadesUser.email,
            username: dadesUser.username,
            password: dadesUser.password,
            url_img: dadesUser.url_img,
            nombre: dadesUser.nombre,
            apellidos: dadesUser.apellidos,
            fecha_nac: dadesUser.fecha_nac,
            followers: dadesUser.followers,
            followings: dadesUser.followings,
            publicacions:dadesUser.publicacions,
            google: true,
            __v: 0
        }
        res.send(dadesFullUser)
    })
})

app.post('/updateUser', (req, res) => {

    const id = req.body.id_user

    const newDadesUser = {
        email: req.body.email,
        username: req.body.username,
        nombre: req.body.name,
        url_img: req.body.url_img,
        apellidos: req.body.surname,
        fecha_nac: req.body.date
    }

    updateDB.updateUser(id, newDadesUser, (newDadesUpdated) => {
        var updatedDadesUser = {
            _id: newDadesUpdated._id,
            email: newDadesUpdated.email,
            username: newDadesUpdated.username,
            password: newDadesUpdated.password,
            url_img: newDadesUpdated.url_img,
            nombre: newDadesUpdated.nombre,
            apellidos: newDadesUpdated.apellidos,
            fecha_nac: newDadesUpdated.fecha_nac,
            followers: newDadesUpdated.followers,
            followings: newDadesUpdated.followings,
            publicacions:newDadesUpdated.publicacions,
            google: req.body.google,
            __v: 0
        }
        res.send(updatedDadesUser)
    })
})




var storageImageUser = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/user_img')
    },
    filename: function (req, file, cb) {
        cb(null, file.originalname + '.png')
    }
})

var uploadImageUser = multer({ storage: storageImageUser });

app.post('/updateUserWithImage', uploadImageUser.single('post'), (req, res, next) => {
    let body = JSON.parse(req.body.PostJson);

    const id = body._id


    let URLServer = "http://localhost:3000/";

    const newDadesUser = {
        email: body.email,
        username: body.username,
        nombre: body.nombre,
        apellidos: body.apellidos,
        url_img: URLServer + req.file.path,
        fecha_nac: body.fecha_nac
    }

    updateDB.updateUser(id, newDadesUser, (newDadesUpdated) => {
        res.send(newDadesUpdated)
    })
})




app.post('/getUser', (req, res) => {
    var email_username = req.body.email_username
    var passwd = req.body.password

    /*var email_username = "A19Narcis"
    var passwd = "Ausias_2003"*/

    var cryptedPasswd = CryptoJS.SHA256(passwd).toString()

    readDB.getUser(email_username, (dades_user_valides) => {
        if (dades_user_valides === null) {
            res.send({ username: false })
        } else {
            console.log("ENVIO DATOS");
            if (dades_user_valides.password === cryptedPasswd) {
                /* Login successful */
                var dadesFinals = {
                    _id: dades_user_valides._id,
                    email: dades_user_valides.email,
                    username: dades_user_valides.username,
                    password: dades_user_valides.password,
                    url_img: dades_user_valides.url_img,
                    nombre: dades_user_valides.nombre,
                    apellidos: dades_user_valides.apellidos,
                    fecha_nac: dades_user_valides.fecha_nac,
                    followers: dades_user_valides.followers,
                    followings: dades_user_valides.followings,
                    publicacions:dades_user_valides.publicacions,
                    google: false,
                    __v: 0
                }
                res.send(dadesFinals)
            } else {
                res.send({ username: false })
            }
        }

    })
})

app.post('/getUsernameAndImageFromID', (req, res) => {
    var id = req.body.id_user;

    /*var id = "644785f8fdc077b15553ba12"*/

    readDB.getUserByID(id, (dades_user) => {
        res.send({ username: dades_user.username, url_img: dades_user.url_img })
    })
})

app.post('/getSelectedUser', (req, res) => {
    var email_username_id = req.body.username

    var googleAccount = req.body.google;

    if (req.body.google === true) {
        googleAccount = true
    } else {
        googleAccount = false
    }

    readDB.getUser(email_username_id, (dades_user) => {
        var dadesFinals = {
            _id: dades_user._id,
            email: dades_user.email,
            username: dades_user.username,
            password: dades_user.password,
            url_img: dades_user.url_img,
            nombre: dades_user.nombre,
            apellidos: dades_user.apellidos,
            fecha_nac: dades_user.fecha_nac,
            followers: dades_user.followers,
            followings: dades_user.followings,
            publicacions:dades_user.publicacions,
            google: googleAccount,
            __v: 0
        }
        res.send(dadesFinals)
    })
})

app.post('/getUserById', (req, res) => {
    var id = req.body.id_user;

    /*var id = "644785f8fdc077b15553ba12"*/

    readDB.getUserByID(id, (dades_user) => {
        res.send(dades_user)
    })
})

/* FOLLOWERS & FOLLOWING */
app.post('/newFollowing', (req, res) => {

    var followingSchema = {};

    //user_following sigue a user_followed
    var user_following = req.body.user_following
    var user_followed = req.body.user_followed

    /*var user_following = 'A19Narcis'
    var user_followed = 'UserTest'*/

    var userFollowedDades = ''

    readDB.getUserByID(user_followed, (dades_user) => {
        userFollowedDades = dades_user
    }).then(() => {
        followingSchema = {
            user: userFollowedDades._id
        }
    }).then(() => {
        updateDB.addFollowingUser(followingSchema, userFollowedDades, user_following, () => {
            res.send({ following: followingSchema.user })
        })
    })
})


app.post('/deleteFollowing', (req, res) => {

    //user_following deja de seguir a user_removed
    var user_following = req.body.user_following
    var user_removed = req.body.user_removed

    /*var user_following = 'A19Narcis'
    var user_removed = 'UserTest'*/

    updateDB.remFollowingUser(user_following, user_removed, () => {
        res.send({ stop_following: user_removed })
    })
})

var storage = multer.diskStorage({
    destination: function (req, file, cb) {
        if (file.mimetype == "video/*") {
            cb(null, 'uploads/videos')
        } else if (file.mimetype == "image/*") {
            cb(null, 'uploads/images')
        }
    },
    filename: function (req, file, cb) {
        let fecha = new Date();
        let dia = fecha.getDate();
        let mes = fecha.getMonth() + 1;
        let anio = fecha.getFullYear();
        let hora = fecha.getHours();
        let minutos = fecha.getMinutes();
        let segundos = fecha.getSeconds();
        let miliseconds = fecha.getMilliseconds();
        if (hora < 10) {
            hora = '0' + hora;
        }
        if (minutos < 10) {
            minutos = '0' + minutos;
        }
        if (segundos < 10) {
            segundos = '0' + segundos;
        }
        let tiempoActual = dia + "_" + mes + "_" + anio + "_" + hora + '_' + minutos + '_' + segundos + "_" + miliseconds
        if (file.mimetype == "video/*") {
            cb(null, file.originalname + "-" + tiempoActual + '.mp4')
        } else if (file.mimetype == "image/*") {
            cb(null, file.originalname + "-" + tiempoActual + '.jpg')
        }
    }
})

var upload = multer({ storage: storage });

app.post('/uploadfile', upload.single('post'), (req, res, next) => {
    const file = req.file;
    if (!file) {
        const error = new Error("Please upload a file");
        error.httpStatusCode = 400;
        console.log("Error", "please upload a file");
        res.send({ code: 500, msg: error });
        return next({ code: 500, msg: error });
    }
    res.send({ code: 200, msg: file });
    addPost(JSON.parse(req.body.PostJson), file.path);
});

app.post('/addPostDubt', (req, res) => {
    addPost(req.body);
    res.send({ code: 200 });
});

/* INSERT PUBLICACIO */
function addPost(body, postUrl) {
    let fecha = new Date();
    let hora = fecha.getHours();
    let minutos = fecha.getMinutes();
    let segundos = fecha.getSeconds();
    if (hora < 10) {
        hora = '0' + hora;
    }
    if (minutos < 10) {
        minutos = '0' + minutos;
    }
    if (segundos < 10) {
        segundos = '0' + segundos;
    }
    let tiempoActual = hora + ':' + minutos + ':' + segundos
    let URLServer = "http://localhost:3000/";

    var post;
    switch (body.type) {
        case ("image"):
            post = {
                tipus: body.type,
                titol: body.title,
                text: body.text,
                hashtags: body.hashtags,
                url_img: URLServer + postUrl,
                url_video: '',
                comentaris: body.comments,
                owner: body.owner,
                hora: tiempoActual
            }
            break;
        case ("doubt"):
            post = {
                tipus: body.type,
                titol: body.title,
                text: body.text,
                hashtags: body.hashtags,
                url_img: '',
                url_video: '',
                comentaris: body.comments,
                owner: body.owner,
                hora: tiempoActual
            }
            break;
        case ("video"):
            post = {
                tipus: body.type,
                titol: body.title,
                text: body.text,
                hashtags: body.hashtags,
                url_img: '',
                url_video: URLServer + postUrl,
                comentaris: body.comments,
                owner: body.owner,
                hora: tiempoActual
            }
            break;
    }

    insertDB.insertPost(post, () => { })
}

app.get('/getSelectedPost/:id', (req, res) => {
    const id = req.params.id

    readDB.getPublicacio(id, (selected_post) => {
        res.send(selected_post)
    })
})

app.get('/getPosts', (req, res) => {
    readDB.getPosts(function (posts) {
        res.send(posts)
    })
})


/* AFEGIR COMENTARI */
app.post('/addNewComment', (req, res) => {

    const id_publi = req.body.id_publi
    const comentari = req.body.comentari

    /*const id_publi = '644624407cd0eca623e9d7c1'
    const comentari = {
        user: '6448e116ee402c11b13bcb4a',
        coment_text: 'Te recomiendo probar ChatGPT'
    }*/

    updateDB.addCommentPost(id_publi, comentari, () => {
        res.send({ commentPosted: true })
    })
})


/* LIKES */
app.post('/newLike', (req, res) => {
    const id_post = req.body.id_post
    const id_user = req.body.id_user

    /*const id_post = "6448e116ee402c11b13bcb4a"
    const id_user = "644785f8fdc077b15553ba12"*/

    updateDB.addLikePost(id_post, id_user, () => {
        res.send({ success: true })
    })
})

app.post('/removeLike', (req, res) => {
    const id_post = req.body.id_post
    const id_user = req.body.id_user

    /*const id_post = "6448e116ee402c11b13bcb4a"
    const id_user = "644785f8fdc077b15553ba12"*/

    updateDB.removeLikePost(id_post, id_user, () => {
        res.send({ success: true })
    })
})


/* DELETE PUBLICACIO */
app.post('/deletePost', (req, res) => {
    const _id = req.body.id_post
    const username = req.body.user

    /*const _id = '6448d4d9fbd18ce084e97d0b';
    const username = 'UserTest'*/

    deleteDB.deletePost(_id, username, () => {
        res.send({ removed: true })
    })
})



/* CHAT */
//Crear el chat
app.post('/createChat', (req, res) => {
    let type = "chat";
    if(req.body.users.length > 2){
        type = "grupo";
    }
    var chat = {
        participants: req.body.users,
        tipo: type
    }
    insertDB.insertChat(chat, () => {
        res.send({ newChat: true })
    })
});


//Update con msg
app.post('/newMessage', (req, res) => {
    let fecha = new Date();
    const formatoFecha = 'ddd MMM DD HH:mm:ss z YYYY';
    let fechaFormateada = getDateJavaFormat();

    var chat_id = req.body.chat_id
    var message = {
        emisor: req.body.emisor,
        txt_msg: req.body.message,
        hora: fechaFormateada
    }

    updateDB.addMessageChat(message, chat_id, function () {
        res.send({ messageSent: true })
    })
});

app.post('/getSuggestedUsersChat', async (req, res) => {
    let usersArr = [];
    let resultUsers = [];

    // Envolver la función `readDB.getUserByID()` en una promesa
    const getUserByIDPromise = (userID) => {
        return new Promise((resolve, reject) => {
            readDB.suggestedUserInfoByID(userID, (dadesUser) => {
                resolve(dadesUser);
            });
        });
    };

    readDB.getUserByID(req.body._id, (users) => {
        for (let i = 0; i < users.followers.length; i++) {
            let flag = false;
            for (let j = 0; j < users.followings.length && !flag; j++) {
                if(users.followers[i].user === users.followings[j].user){
                    usersArr.push(users.followers[i].user);
                    flag = true;
                }
            }
        }

        // Utilizar `Promise.all()` para esperar a que todas las promesas se resuelvan antes de continuar con la ejecución del código
        Promise.all(usersArr.map(userID => getUserByIDPromise(userID)))
            .then((dadesUsers) => {
                resultUsers = dadesUsers;
                res.send(resultUsers);
            })
            .catch((error) => {
                console.log(error);
                res.send(error);
            });
    });       
});

//Crear el chat
app.post('/getMessages', (req, res) => {
    readDB.getChat(req.body.chat_id, (chat) => {
        res.send({ chat: chat })
    })
});

app.post('/getAllMyChats', (req, res) => {
    readDB.getAllMyChats(req.body._id, (todosLosChats) => {
        res.send(todosLosChats);
    })
});


/* BUSCADOR Usuarios - Publicaciones - Dudas */

app.post('/searchUsers', (req, res) => {
    const resultText = req.body.username;
    readDB.getUsers(resultText, (users) => {
        res.send(users);
    })
})

app.post('/searchPost', (req, res) => {
    const hashtag = req.body.hashtag

    readDB.getPostsByHashtag(hashtag, (allPosts) => {
        res.send(allPosts);
    })
})


app.post('/searchDoubt', (req, res) => {
    const query = req.body.query;

    readDB.getPostsByQuery(query, (allDoubts) => {
        res.send(allDoubts)
    })
})


app.listen(PORT, () => {
    console.log("Server Running [" + PORT + "]");
});

function getDateJavaFormat(){
    const fechaActual = new Date();

    // Días de la semana
    const diasSemana = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    const diaSemana = diasSemana[fechaActual.getUTCDay()];

    // Meses
    const meses = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    const mes = meses[fechaActual.getUTCMonth()];

    // Día del mes
    const dia = fechaActual.getUTCDate();

    // Hora, minutos y segundos
    const hora = fechaActual.getUTCHours();
    const minutos = fechaActual.getUTCMinutes();
    const segundos = fechaActual.getUTCSeconds();

    // Zona horaria
    const zonaHoraria = 'PDT'; // Puedes cambiarla según la zona horaria deseada

    // Año
    const año = fechaActual.getUTCFullYear();

    // Formatear la fecha actual según el formato especificado
    const fechaFormateada = `${diaSemana} ${mes} ${dia} ${hora}:${minutos}:${segundos} ${zonaHoraria} ${año}`;

    return fechaFormateada;
}