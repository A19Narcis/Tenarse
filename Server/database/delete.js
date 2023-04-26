const mongoose = require('mongoose');
const path = require('path');
const fs = require('fs')

const { User } = require('./connection')
const { Post } = require('./connection')
const { Chat } = require('./connection')

const readDB = require('./read')

const deletePost = async function (idPost, username, callback) {
    readDB.getPublicacio(idPost, (deletedPost) => {
        const path_inicial = deletedPost.url_img;
        const path_final = path_inicial.replace('http://localhost:3000', path.join(__dirname, '..', ''));
        fs.unlink(path_final, (err) => {
            if (err) throw err;
        });
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