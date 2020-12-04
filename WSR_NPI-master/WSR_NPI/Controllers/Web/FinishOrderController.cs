﻿using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Http;
using WSR_NPI.Crypt;
using WSR_NPI.DataBase;
using WSR_NPI.Models;

namespace WSR_NPI.Controllers.Web
{
    public class FinishOrderController : ApiController
    {
        public string Post(string token)
        {
            Context db = new Context();
            BaseMethods bM = new BaseMethods();
            var user = db.Users.FirstOrDefault(x => x.Token.Equals(token)); //ищем пользователя

            if (user != null)
            {
                //освобождаем курьера и закрываем заказ
                var courier = db.Сouriers.FirstOrDefault(x => x.User.Id == user.Id);
                courier.Status = "Свободен";
                var order = db.Orders.FirstOrDefault(x => x.Id == courier.OrderId);
                order.Status = "Доставлен";
                //добавляем запись в блокчейн
                BlockChainManager.GenerateNextBlock(bM.Encrypt(JsonConvert.SerializeObject(order)), user.Id);
                courier.OrderId = null;
                db.SaveChanges();

                return "Успешно";
            }
            else
            {
                return null;
            }
        }
    }
}