﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Data.Entity;
using System.Web;
using System.Web.Mvc;
using WSR_NPI.DataBase;
using WSR_NPI.Models;
using WSR_NPI.DataBase.Models;
using Newtonsoft.Json;
using WSR_NPI.Crypt;
using Microsoft.Scripting.Hosting;
using IronPython.Hosting;

namespace WSR_NPI.Controllers
{
    [Authorize(Roles = "Менеджер по продажам")]
    public class SalesManagerController : Controller
    {
        private Context Context = new Context();
        BaseMethods bM = new BaseMethods();
        // GET: SalesManager
        public ActionResult Index()
        {
            return View(Context.Orders.Include(o => o.User));
            
        }

        /// <summary>
        /// Добавление заказа
        /// </summary>
        /// <returns></returns>
        [Authorize]
        [HttpGet]
        public ActionResult CreateOrder()
        {
            var model = new CreateOrderViewModel();
            var orderNums = new List<CreateOrderNum>();

            foreach (var nomenclature in Context.Nomenclatures)
            {
                orderNums.Add(new CreateOrderNum
                {
                    Name = nomenclature.Name,
                    Count = nomenclature.Count,
                    IsBuy = false
                });
            }

            model.OrderNums = orderNums;
            return View(model);
        }

        /// <summary>
        /// Добавление заказа
        /// </summary>
        /// <param name="model"></param>
        /// <returns></returns>
        [Authorize]
        [HttpPost]
        public ActionResult CreateOrder(CreateOrderViewModel model)
        {
            if (ModelState.IsValid)
            {

                var errorOrderNums = model.OrderNums.Where(o => (o.CountBuy > o.Count) || o.CountBuy < 0 || (o.IsBuy && o.CountBuy == 0));

                if (errorOrderNums.Any())
                {
                    ModelState.AddModelError("", "Проверьте список заказа, вы указали недопустимое число товара");

                    return View(model);
                };

                if (model.OrderNums.Where(x => x.IsBuy).Count() == 0) 
                {
                    ModelState.AddModelError("", "Выберите товар");

                    return View(model);
                }

                var user = Context.Users.Single(u => u.Login.Equals(User.Identity.Name));

                var order = Context.Orders.Add(new Order
                {
                    Adres = model.Adress,
                    UserId = user.Id,
                    Status = "Принят"
                });


                if (SmartCreate(order))
                {
                    var orderData = bM.Encrypt(JsonConvert.SerializeObject(order));
                    ScriptEngine engine = Python.CreateEngine();
                    ScriptScope scope = engine.CreateScope();
                    scope.SetVariable("msg", orderData);
                    engine.ExecuteFile(Server.MapPath("~/Py/keys.py"), scope);
                    dynamic sign = scope.GetVariable("sign");
                    dynamic pubKey = scope.GetVariable("pubKey");

                    BlockChainManager.Path = Server.MapPath("~/Py/varify.py");
                    BlockChainManager.Sign = sign;
                    BlockChainManager.PubKey = pubKey;
                    BlockChainManager.Need = true;
                    BlockChainManager.GenerateNextBlock(orderData, user.Id);
                }

                foreach (var orderNumModel in model.OrderNums.Where(o => o.IsBuy && o.CountBuy > 0))
                {
                    var nomenclature = Context.Nomenclatures.Single(n => n.Name.Equals(orderNumModel.Name));


                    Context.OrderNoms.Add(new OrderNom
                    {
                        OrderId = order.Id,
                        NomenclatureId = nomenclature.Id,
                        CountInOrder = orderNumModel.CountBuy
                    });

                    nomenclature.Count -= orderNumModel.CountBuy;
                    Context.SaveChanges();
                }

                return RedirectToAction("Index");
            }

            return View(model);
        }



        /// <summary>
        /// Содержимое заказа
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        /// 
        public ActionResult Details(int id)
        {
            var order = Context.Orders.Include(o => o.OrderNoms).Include(o => o.OrderNoms.Select(x => x.Nomenclature)).FirstOrDefault(o => o.Id == id);

            if (order != null)
            {
                return PartialView("Details", order);
            }

            return View("Index");
        }


        /// <summary>
        /// Редактирование заказа
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public ActionResult Edit(int id)
        {
            var model = new CreateOrderViewModel();
            var orderNums = new List<CreateOrderNum>();

            foreach (var nomenclature in Context.Nomenclatures)
            {
                orderNums.Add(new CreateOrderNum
                {
                    Name = nomenclature.Name,
                    Count = nomenclature.Count,
                    IsBuy = false
                });
            }

            var order = Context.Orders
                .Include(x => x.OrderNoms)
                .Include(x => x.OrderNoms.Select(y => y.Nomenclature))
                .Single(x => x.Id == id);

            foreach (var orderNom in order.OrderNoms)
            {

                var orderNomResult = orderNums.Single(x => x.Name.Equals(orderNom.Nomenclature.Name));

                orderNomResult.IsBuy = true;
                orderNomResult.CountBuy = orderNom.CountInOrder;
                orderNomResult.Count += orderNom.CountInOrder;
            }

            model.OrderNums = orderNums;
            model.Adress = order.Adres;

            return View(model);
        }

        /// <summary>
        /// Редактирование заказа
        /// </summary>
        /// <param name="id"></param>
        /// <param name="model"></param>
        /// <returns></returns>
        [HttpPost]
        public ActionResult Edit(int id, CreateOrderViewModel model)
        {
            try
            {
                if (ModelState.IsValid)
                {
                    var errorOrderNums = model.OrderNums.Where(o => (o.CountBuy > o.Count) || o.CountBuy < 0 || (o.IsBuy && o.CountBuy == 0));

                    if (errorOrderNums.Any())
                    {
                        ModelState.AddModelError("", "Проверьте список заказа, вы указали недопустимое число товара");

                        return View(model);
                    };

                    var order = Context.Orders
                        .Include(x => x.OrderNoms)
                        .Include(x => x.OrderNoms.Select(y => y.Nomenclature))
                        .Single(x => x.Id == id);

                    foreach (var orderNumModel in model.OrderNums)
                    {
                        var orderNom = order.OrderNoms.FirstOrDefault(x => x.Nomenclature.Name.Equals(orderNumModel.Name));
                        var nomenclature = Context.Nomenclatures.Single(n => n.Name.Equals(orderNumModel.Name));

                        if (orderNom != null)
                        {
                            if (orderNumModel.IsBuy)
                            {
                                nomenclature.Count += orderNom.CountInOrder;
                                orderNom.CountInOrder = orderNumModel.CountBuy;
                                nomenclature.Count -= orderNom.CountInOrder;
                            }
                            else
                            {
                                nomenclature.Count += orderNom.CountInOrder;
                                Context.OrderNoms.Remove(orderNom);
                            }

                            Context.SaveChanges();
                        }
                        else
                        {
                            if (orderNumModel.IsBuy)
                            {
                                Context.OrderNoms.Add(new OrderNom
                                {
                                    OrderId = order.Id,
                                    NomenclatureId = nomenclature.Id,
                                    CountInOrder = orderNumModel.CountBuy
                                });

                                nomenclature.Count -= orderNumModel.CountBuy;
                                Context.SaveChanges();
                            }
                        }
                    }


                    return RedirectToAction("Index");
                }

                return View(model);
            }
            catch
            {
                ModelState.AddModelError("", "Ошибка");
                return View(model);
            }
        }

        /// <summary>
        /// Отмена заказа
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public ActionResult Cancel(int id)
        {
            try
            {
                var order = Context.Orders
                           .Include(x => x.OrderNoms)
                           .Include(x => x.OrderNoms.Select(y => y.Nomenclature))
                           .Single(x => x.Id == id);

                foreach (var orderNom in order.OrderNoms)
                {
                    var nomenclature = Context.Nomenclatures.Single(n => n.Id == orderNom.Nomenclature.Id);
                    nomenclature.Count += orderNom.CountInOrder;

                    Context.SaveChanges();
                }

                order.Status = "Отменен";
                var user = Context.Users.Single(x => x.Login.Equals(User.Identity.Name));

                if (SmartCancel(order)){
                    BlockChainManager.GenerateNextBlock(bM.Encrypt(JsonConvert.SerializeObject(order)), user.Id);
                }

                Context.SaveChanges();

                return RedirectToAction("Index");
            }
            catch
            {
                return View();
            }
        }

        /// <summary>
        /// Отображение истории смены статусов заказа
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public ActionResult History(int id)
        {
            Context = new Context();
            var model = new List<Block>();

            var blocks = Context.Blocks.ToList();

            foreach(var block in blocks)
            {
                if(block.Index != 1)
                {
                    var order = JsonConvert.DeserializeObject<Order>(bM.Decrypt(block.Data));
                    if (order.Id == id)
                    {
                        model.Add(block);
                    }
                }
            }

            return PartialView("History", model);           
        }

        /// <summary>
        /// Освобождение ресурсов
        /// </summary>
        /// <param name="disposing"></param>
        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                Context.Dispose();
            }
            base.Dispose(disposing);
        }

        /// <summary>
        /// Смарт контракт для создания заказа
        /// </summary>
        /// <param name="order"></param>
        /// <returns></returns>
        private bool SmartCreate(Order order)
        {
            if (Context.Orders.FirstOrDefault(x => x.Id == order.Id) == null && order.Status.Equals("Принят"))
            {
                return true;
            }
            else {
                return false;
            }
        }

        /// <summary>
        /// Смарт контракт для отмены заказа
        /// </summary>
        /// <param name="order"></param>
        /// <returns></returns>
        private bool SmartCancel(Order order)
        {
            var o = Context.Orders.FirstOrDefault(x => x.Id == order.Id);

            if (o != null && (o.Status.Equals("Принят") || o.Status.Equals("Комплектация начата") || o.Status.Equals("Комплектация завершена")))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}