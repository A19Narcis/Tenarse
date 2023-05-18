const mongoose = require('mongoose');
const path = require('path');
const fs = require('fs')

const { User } = require('./connection')
const { Post } = require('./connection')
const { Chat } = require('./connection')

const readDB = require('./read')

const deletePost = async function (idPost, username, callback) {
    readDB.getPublicacio(idPost, (deletedPost) => {
        if (deletedPost.tipus == 'image') {
            const path_inicial = deletedPost.url_img;
            const path_final = path_inicial.replace('http://localhost:3000', path.join(__dirname, '..', '')); //localhost:3000 -> 212.227.40.235
            console.log(path_final);
            fs.unlink(path_final, (err) => {
                if (err) throw err;
            });
        } else if (deletedPost.tipus == 'video') {
            const path_inicial = deletedPost.url_video;
            const path_final = path_inicial.replace('http://localhost:3000', path.join(__dirname, '..', ''));
            fs.unlink(path_final, (err) => {
                if (err) throw err;
            });
        }
    })  
    await Post.deleteOne({ _id: idPost });
    await User.updateOne(
        { username: username },
        { $pull: { publicacions: { _id: idPost } } }
    ).then(result => {
        callback();
    })
}


module.exports = {
    deletePost
}