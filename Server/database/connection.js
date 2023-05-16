const mongoose = require("mongoose")
const { User, Post, Chat } = require("./esquemas");


const user = "a19nargomcar"
const passwd = "Pedralbes22_23"
const host = "212.227.40.235"
const puerto = "27017"

const URL = 'mongodb+srv://' + user + ':' + passwd + '@tenarse.lyvrboh.mongodb.net/' //ATLAS
//const URL = 'mongodb://' + user + ':' + passwd + '@' + host + ':' + puerto + '/' //TENARSE SERVER
//const URL = 'mongodb://127.0.0.1:27017/Tenarse'
const options = {
    dbName: 'Tenarse',
    connectTimeoutMS: 5000
};

main().catch(err => console.log(err));


async function main() {
    try {
        await mongoose.connect(URL, options).then(
            async () => {
                console.log('Connexió establerta al MongoDB!')
            },
            err => {
                console.log('No es pot connectar al MongoDB...');
            }
        );

        //Close connection
        /*mongoose.connection.close(function(){
            console.log('Connexió amb la BD tancada');
            process.exit(0);
        });*/

    } catch (error) {
        console.log(error);
    }
}


module.exports = {
    User,
    Post,
    Chat
}