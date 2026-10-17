var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
const mongoose = require('mongoose');
const morgan = require('morgan');

const app = express();

MONGO_URI = 'mongodb://localhost:27017/gestion_etudiants';

app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(morgan('dev'));
app.use(express.static(path.join(__dirname, '../client')));

mongoose.connect(MONGO_URI);

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

var indexRouter = require('./routes/index');
const etudiantRoutes = require('./routes/students');
const groupeRoutes = require('./routes/groups');
const errorMiddleware = require('./middlewares/errorMiddleware');

app.use('/students', etudiantRoutes);
app.use('/groups', groupeRoutes);
app.use('/', indexRouter);
app.use(errorMiddleware);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Serveur lancé sur http://localhost:${PORT}`);
});

app.use(function(req, res, next) {
    next(createError(404));
  });
  

module.exports = app;