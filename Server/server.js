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

const PORT = 3000

// Carpeta imagenes
const publicDirPosts = path.join(__dirname, 'uploads', 'images');
const publicDirUsers = path.join(__dirname, 'uploads', 'user_img');

// Ruta de la carpeta
app.use('/uploads/images', express.static(publicDirPosts));
app.use('/uploads/user_img', express.static(publicDirUsers));

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

    const usuari = {
        email: req.body.email,
        username: req.body.username,
        password: CryptoJS.SHA256(req.body.passwd).toString(),
        url_img: 'http://localhost:3000/uploads/user_img/TeoX.png',
        nombre: req.body.name,
        apellidos: req.body.surname,
        fecha_nac: req.body.date
    }
    /*
        const usuari = {
            username: 'A19Narcis',
            password: CryptoJS.SHA256(noHashedPassword).toString(),
            url_img: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHsAAAB7CAMAAABjGQ9NAAAAJFBMVEX////d3d3c3Nz4+Pjm5ub7+/vh4eHq6ur19fXy8vLu7u7Z2dk19XF3AAAEzUlEQVRogcVb2QKkIAxbuY///98Fj/GEphV38zijRmhpG6h//ghgQ0zemwXepxis5DFc6OideoLzUX/Ia6OZJqWmZ9Q/TPxkAmxybd4Dv0uj6YNRJPFGr0wYyJxcxohX+uzSKGYW8UY/gj1NfOaZfXrLHp2MeWZ38QWzfcG8sIt9PoKu3SFXsqFbl18yV2TJ0N8PeoFg6H7EoBdkz6M2Ywa9QBkG81v/vpHjRh9NzSC3wkjWJZ8gcjueeWYHyO0nzBUk+Xhbb6Bt/hl1Je9TD13XN/LuOk9fUhfyTkoP31IX8nYl9zV1If8vxl65GyaP41JXG/kxo34Uz654jG//YMZn7odZZ/p4lX+5AJUrhzvvvs4JaEV1uqp8tQ7RG0CnnW6+hbcI317lrj4ZrepTzptf3Q1nflR6liUizveiwTSbpsCP8NgvoRXUt1NX3SY0QJyiG2ZtRRW7Gpz4k8Whe5AiHwsSR1fX0GRBuwkeIs+adYMCd5Eg8oP1HOtNCUDT/pv0QE85R9AhzpM3A9JvSnr4EUiVvWUUS085UWFegKzYtWCmM1inzHoEYPL1kaRnsjRshQa4l7hKTnlmb1sAC202I+ka7GFj9VcdEGlufGnvAJZONTidPvnUiP8m4BV7QqYNmrtaknI17gJbQE+6A96wKWO6oC2pAJfkxbQNdFouIoHyClYoP4DOT5oMvjJXAwrfkhpJbuHhBxktCzflFGJu0ofTZ9zkIvvP3FTK+Y7b09zCAxfa1wBu4RpDakDS3vzsPYOOa7SvCWMqlETJohLZfL6DrpqAuCZ0NrruLs8lJ0dkcAtInQDUs1nADexjFHFJl5SSSQfUZfUj+iq+pwPqci5B6Vd83gPtDhtRZH9wDcEAIgbnegi4kBtXaebVizRyIUuaYHsu8yMRn+TMOrYPvzwR2SBhkANhZfpFLOg94VIZPAJY1w6g1HFyDe6qbh4EGBwlR3c1f8sW80vkEBtukvgNBNvSJLeROU0S+34CvO+eTWfo+Ab6cdngx6BK+QY76+DgGCgZp3JqMvfuRJsc67zmWBKwzsaUcibupzU21I4+xv2XUgj1tp0+T87VLk3nVGYfkZ13riTH/Ut/Jv++a4D+/OT7xH1Zqh/2Odyor3mJCuoKNCtw3b0Y6Lh67Tr1KWgMIfnuCelDXmjmvnoKyZVFtvaztp738LDnU70sbniMz31/z82jD+6W3ZsW2/DA3iiAbu6mpLr/h3Sb+VbVeUkpyr1v6r7WEu3RnC6UbjhccFo/vZKT9AkBTj7cuW43OV+DNbHXwX2JsQmkgdQ7OSWmF3977eCyh9aidZCb7agOh9TY5TrZtlYPDhxPWWkjrV0RYUFnxnTs70gZt6JX3P7l/uMy59ClxOGeCmDBGmZeKMLqVQrbERy7A11XCTKA2pdMxk9JZarknyqssGXQolhR5v2lyxUnkx472Popj3ypx/pxj3zm4iT2uepj72KU9TL2mbkll2Ho4nPc76Lqd1eq3WXHYq/dmPCj6suqTn8fF2nKRet4eu5DCcdFH49NB9GoefSpqVFsSPOIlRmdBddO0Cq4nUlRa7u9g7VVg81dquXPj75WrEOrMi/XwU1ux+8n//F3onbVmSfMOvWffJ86v4IOcUHQUtK/HYswl1HlW4MAAAAASUVORK5CYII=',
            nombre: 'Narcis',
            apellidos: 'Gomez Carretero',
            fecha_nac: '28/08/2003'
        }
    */
    insertDB.insertUsuari(usuari, function (status) {
        res.send({ result: status });
    })
})

app.post('/getUser', (req, res) => {
    var email_username = req.body.email_username
    var passwd = req.body.password

    /*var username = "A19Narcis"
    var passwd = "ausias2003"*/

    var cryptedPasswd = CryptoJS.SHA256(passwd).toString()

    readDB.getUser(email_username, (dades_user_valides) => {
        if (dades_user_valides === null) {
            res.send({ login: false })
        } else {
            if (dades_user_valides.password === cryptedPasswd) {
                /* Login successful */
                res.send({ login: true })
            } else {
                res.send({ login: false })
            }
        }

    })
})

/* FOLLOWERS & FOLLOWING */
app.post('/newFollowing', (req, res) => {

    var followingSchema = {};

    //_DevOps_ sigue a A19Narcis
    var user_following = '_DevOps_'
    var user_followed = 'A19Narcis'

    var userFollowedDades = ''

    readDB.getUser(user_followed, (dades_user) => {
        userFollowedDades = dades_user
    }).then(() => {
        followingSchema = {
            user: userFollowedDades.username
        }
    }).then(() => {
        updateDB.addFollowingUser(followingSchema, userFollowedDades, user_following, () => {
            res.send({ following: followingSchema.user })
        })
    })
})


app.post('/deleteFollowing', (req, res) => {

    //_DevOps_ deja de seguir a A19Narcis
    var user_following = 'A19Narcis'
    var user_removed = '_DevOps_'

    updateDB.remFollowingUser(user_following, user_removed, () => {
        res.send({ stop_following: user_removed })
    })


})

/* INSERT PUBLICACIO */
app.post('/addNewPost', (req, res) => {
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

    //Llamnar a las imagenes de los posts -> user + hora.png EX: A19Narcis_091232.png
    const post = {
        tipus: 'doubt',
        titol: 'How to substract numeric and alphanumeric value in python?',
        text: 'I have 2 column with numeric and alphanumeric value. I want to apply substraction on numeric value in third column and keep aplhanumeric value as "Canadian". Please help',
        url_img: '',
        url_video: '',
        comentaris: [],
        owner: 'A19Narcis',
        user_img: 'http://localhost:3000/uploads/user_img/default_user_img.png',
        hora: tiempoActual
    }
    /*const post = {
        tipus: 'image',
        titol: '',
        text: 'Mi primer post en esta red social.',
        url_img: 'http://localhost:3000/uploads/images/JavaScript_code.png', 
        url_video: '',
        comentaris: [],
        owner: 'TeoX',
        user_img: 'http://localhost:3000/uploads/user_img/TeoX.png',
        hora: tiempoActual
    }*/

    insertDB.insertPost(post, function () {
        res.send({ success: true });
    })
})


app.get('/getPosts', (req, res) => {
    readDB.getPosts(function (posts) {
        res.send(posts)
    })
})


/* AFEGIR COMENTARI */
app.post('/addNewComment', (req, res) => {
    const id_publi = '6422ca14aafc9f2312ec1b1d'
    const comentari = {
        user_img: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHsAAAB7CAMAAABjGQ9NAAAAJFBMVEX////d3d3c3Nz4+Pjm5ub7+/vh4eHq6ur19fXy8vLu7u7Z2dk19XF3AAAEzUlEQVRogcVb2QKkIAxbuY///98Fj/GEphV38zijRmhpG6h//ghgQ0zemwXepxis5DFc6OideoLzUX/Ia6OZJqWmZ9Q/TPxkAmxybd4Dv0uj6YNRJPFGr0wYyJxcxohX+uzSKGYW8UY/gj1NfOaZfXrLHp2MeWZ38QWzfcG8sIt9PoKu3SFXsqFbl18yV2TJ0N8PeoFg6H7EoBdkz6M2Ywa9QBkG81v/vpHjRh9NzSC3wkjWJZ8gcjueeWYHyO0nzBUk+Xhbb6Bt/hl1Je9TD13XN/LuOk9fUhfyTkoP31IX8nYl9zV1If8vxl65GyaP41JXG/kxo34Uz654jG//YMZn7odZZ/p4lX+5AJUrhzvvvs4JaEV1uqp8tQ7RG0CnnW6+hbcI317lrj4ZrepTzptf3Q1nflR6liUizveiwTSbpsCP8NgvoRXUt1NX3SY0QJyiG2ZtRRW7Gpz4k8Whe5AiHwsSR1fX0GRBuwkeIs+adYMCd5Eg8oP1HOtNCUDT/pv0QE85R9AhzpM3A9JvSnr4EUiVvWUUS085UWFegKzYtWCmM1inzHoEYPL1kaRnsjRshQa4l7hKTnlmb1sAC202I+ka7GFj9VcdEGlufGnvAJZONTidPvnUiP8m4BV7QqYNmrtaknI17gJbQE+6A96wKWO6oC2pAJfkxbQNdFouIoHyClYoP4DOT5oMvjJXAwrfkhpJbuHhBxktCzflFGJu0ofTZ9zkIvvP3FTK+Y7b09zCAxfa1wBu4RpDakDS3vzsPYOOa7SvCWMqlETJohLZfL6DrpqAuCZ0NrruLs8lJ0dkcAtInQDUs1nADexjFHFJl5SSSQfUZfUj+iq+pwPqci5B6Vd83gPtDhtRZH9wDcEAIgbnegi4kBtXaebVizRyIUuaYHsu8yMRn+TMOrYPvzwR2SBhkANhZfpFLOg94VIZPAJY1w6g1HFyDe6qbh4EGBwlR3c1f8sW80vkEBtukvgNBNvSJLeROU0S+34CvO+eTWfo+Ab6cdngx6BK+QY76+DgGCgZp3JqMvfuRJsc67zmWBKwzsaUcibupzU21I4+xv2XUgj1tp0+T87VLk3nVGYfkZ13riTH/Ut/Jv++a4D+/OT7xH1Zqh/2Odyor3mJCuoKNCtw3b0Y6Lh67Tr1KWgMIfnuCelDXmjmvnoKyZVFtvaztp738LDnU70sbniMz31/z82jD+6W3ZsW2/DA3iiAbu6mpLr/h3Sb+VbVeUkpyr1v6r7WEu3RnC6UbjhccFo/vZKT9AkBTj7cuW43OV+DNbHXwX2JsQmkgdQ7OSWmF3977eCyh9aidZCb7agOh9TY5TrZtlYPDhxPWWkjrV0RYUFnxnTs70gZt6JX3P7l/uMy59ClxOGeCmDBGmZeKMLqVQrbERy7A11XCTKA2pdMxk9JZarknyqssGXQolhR5v2lyxUnkx472Popj3ypx/pxj3zm4iT2uepj72KU9TL2mbkll2Ho4nPc76Lqd1eq3WXHYq/dmPCj6suqTn8fF2nKRet4eu5DCcdFH49NB9GoefSpqVFsSPOIlRmdBddO0Cq4nUlRa7u9g7VVg81dquXPj75WrEOrMi/XwU1ux+8n//F3onbVmSfMOvWffJ86v4IOcUHQUtK/HYswl1HlW4MAAAAASUVORK5CYII=',
        user: 'A19Narcis',
        coment_text: 'Te recomiendo probar ChatGPT'
    }

    updateDB.addCommentPost(id_publi, comentari, () => {
        res.send({ commentPosted: true })
    })
})


/* DELETE PUBLICACIO */
app.post('/deletePost', (req, res) => {
    const _id = '6422ca14aafc9f2312ec1b1d';
    const username = '_DevOps_'
    deleteDB.deletePost(_id, username, () => {
        res.send({ removed: true })
    })
})



/* CHAT */
//Crear el chat
app.post('/createChat', (req, res) => {
    var chat = {
        participants: ['A19Narcis', '_DevOps_'],
    }
    insertDB.insertChat(chat, () => {
        res.send({ newChat: true })
    })
})


//Update con msg
app.post('/newMessage', (req, res) => {
    let fecha = new Date();
    let hora = fecha.getHours();
    let minutos = fecha.getMinutes();
    let horaActual = hora + ":" + minutos;

    var chat_id = '6422957c931b6cd18c021533'
    var message = {
        emisor: 'A19Narcis',
        txt_msg: '¡Buenas!',
        hora: horaActual
    }

    updateDB.addMessageChat(message, chat_id, function () {
        res.send({ messageSent: true })
    })
})



app.listen(PORT, () => {
    console.log("Server Running [" + PORT + "]");
});