package orderBookUpdated15;

import java.util.PriorityQueue;

public class BuySideMatch
{
	private PriorityQueue<Order> buySideOrder = new PriorityQueue<Order>();
	private PriorityQueue<Order> sellSideOrder = new PriorityQueue<Order>();
	private PriorityQueue<Order> processedOrder = new PriorityQueue<Order>();
	
	public BuySideMatch()
	{
		
	}
	
	public BuySideMatch(PriorityQueue<Order> buySideOrder, PriorityQueue<Order> sellSideOrder)
	{
		this.buySideOrder = buySideOrder;
		this.sellSideOrder = sellSideOrder;
	}
	
	public void setBQ(PriorityQueue<Order> buySideOrder)
	{
		this.buySideOrder = buySideOrder;
	}
	public PriorityQueue<Order> getBQ()
	{
		return buySideOrder;
	}
	
	public void setSQ(PriorityQueue<Order> sellSideOrder)
	{
		this.sellSideOrder = sellSideOrder;
	}
	public PriorityQueue<Order> getSQ()
	{
		return sellSideOrder;
	}
	
	public PriorityQueue<Order> matchOrder()
	{
		 if(buySideOrder.peek().isMarketOrder()&&sellSideOrder.peek() == null)
		 {
			 //System.out.println("NO LIQUIDITY " + buySideOrder.poll() + " is Canceled");
			 //System.out.println("---Updated--- " + buySideOrder);
			 buySideOrder.peek().setStatus(3);
			 processedOrder.add(buySideOrder.poll());
			 System.out.println(processedOrder);
			 return processedOrder;
			 
		 }
		 else if (buySideOrder.peek().isMarketOrder()&&sellSideOrder.peek().isLimitOrder())
		 {
			if(buySideOrder.peek().getVolume() == sellSideOrder.peek().getVolume())
			{
				//System.out.println("Volume " + buySideOrder.peek().getVolume() + " deal with Price " + sellSideOrder.peek().getPrice());
				//System.out.println(buySideOrder.poll() + " -----is removed");
				//System.out.println(sellSideOrder.poll()+ " -----is removed");
				buySideOrder.peek().setStatus(1);
				sellSideOrder.peek().setStatus(1);
				
				buySideOrder.peek().setDealingPrice(sellSideOrder.peek().getPrice());
				
				processedOrder.add(buySideOrder.poll());
				processedOrder.add(sellSideOrder.poll());
				
				return processedOrder;
			}
			else if(buySideOrder.peek().getVolume() < sellSideOrder.peek().getVolume())
			{
				int aVolume = sellSideOrder.peek().getVolume() - buySideOrder.peek().getVolume();
				sellSideOrder.peek().setVolume(aVolume);
				//System.out.println("Volume " + buySideOrder.peek().getVolume() + " deal with Price " + sellSideOrder.peek().getPrice());
				//System.out.println(buySideOrder.poll() + " -----is removed");
				buySideOrder.peek().setStatus(1);
				buySideOrder.peek().setDealingPrice(sellSideOrder.peek().getPrice());
				processedOrder.add(buySideOrder.poll());
				
				return processedOrder;
			}
			else
			{
				double sumBuyPrice = 0;
				int sumBuyVolume = 0;
				
				while(buySideOrder.peek().getVolume() > sellSideOrder.peek().getVolume())
				{
					int bVolume = buySideOrder.peek().getVolume() - sellSideOrder.peek().getVolume();
					buySideOrder.peek().setVolume(bVolume);
					sumBuyPrice += (sellSideOrder.peek().getVolume())*(sellSideOrder.peek().getPrice());
					sumBuyVolume += sellSideOrder.peek().getVolume();
					
					sellSideOrder.peek().setStatus(1);
					processedOrder.add(sellSideOrder.poll());
					//System.out.println(sellSideOrder.poll() + " -----is removed");
					if(sellSideOrder.peek() == null)
					{
						//System.out.println("No Enough Liquidity, Partly Filled " + sumBuyVolume + " with Price " + (sumBuyPrice/sumBuyVolume));
						//System.out.println("Rest Of " + buySideOrder.poll() + " -----is Canceled");
						buySideOrder.peek().setStatus(2);
						buySideOrder.peek().setVolume(sumBuyVolume);
						buySideOrder.peek().setDealingPrice(sumBuyPrice/sumBuyVolume);
						processedOrder.add(buySideOrder.poll());
						
						return processedOrder;
					}
					else
					{
						if(buySideOrder.peek().getVolume() < sellSideOrder.peek().getVolume())
						{
							int cVolume = sellSideOrder.peek().getVolume() - (buySideOrder.peek().getVolume());
							sellSideOrder.peek().setVolume(cVolume);
							double allPrice = sumBuyPrice+ (sellSideOrder.peek().getPrice())*(buySideOrder.peek().getVolume());
							int allVolume = sumBuyVolume + (buySideOrder.peek().getVolume());
							double averagePrice = (allPrice)/(allVolume);
							
							buySideOrder.peek().setStatus(1);
							buySideOrder.peek().setDealingPrice(allPrice);
							buySideOrder.peek().setVolume(allVolume);
							//System.out.println("Volume " + allVolume + " deal with Price " + averagePrice);
							//System.out.println(buySideOrder.poll() + " -----is removed");
							processedOrder.add(buySideOrder.poll());
							
							return processedOrder;
						}
						else if(buySideOrder.peek().getVolume() == sellSideOrder.peek().getVolume())
						{
							//System.out.println("-----removed volume " + buySideOrder.peek().getVolume() + " from " + sellSideOrder.peek());
							double allPrice = sumBuyPrice+ (sellSideOrder.peek().getPrice())*(buySideOrder.peek().getVolume());
							int allVolume = sumBuyVolume + (buySideOrder.peek().getVolume());
							double averagePrice = (allPrice)/(allVolume);
							
							sellSideOrder.peek().setStatus(1);
							buySideOrder.peek().setStatus(1);
							buySideOrder.peek().setDealingPrice(averagePrice);
							buySideOrder.peek().setVolume(allVolume);

							processedOrder.add(buySideOrder.poll());
							processedOrder.add(sellSideOrder.poll());
							
							return processedOrder;
						}
						else
							continue;
					 }
				  }
			   }
		 }
		 else if(((buySideOrder.peek()!=null) &&buySideOrder.peek().isLimitOrder())&&((sellSideOrder.peek()!=null) &&sellSideOrder.peek().isLimitOrder()))
		 {
			 if(buySideOrder.peek().getPrice() == sellSideOrder.peek().getPrice())
			 {
				 if(buySideOrder.peek().getVolume() == sellSideOrder.peek().getVolume())
				 {
					 //System.out.println("Volume " + buySideOrder.peek().getVolume() + " deal with Price " + sellSideOrder.peek().getPrice());
					 // System.out.println(buySideOrder.poll() + " -----is removed");
					 // System.out.println(sellSideOrder.poll()+ " -----is removed");
					 buySideOrder.peek().setStatus(1);
					 sellSideOrder.peek().setStatus(1);
						
					 processedOrder.add(buySideOrder.poll());
					 processedOrder.add(sellSideOrder.poll());
					 
					 return processedOrder;
				 }
				 else if(buySideOrder.peek().getVolume() < sellSideOrder.peek().getVolume())
				 {
					 int aVolume = sellSideOrder.peek().getVolume() - buySideOrder.peek().getVolume();
					 sellSideOrder.peek().setVolume(aVolume);
					 
					 buySideOrder.peek().setStatus(1);
					 processedOrder.add(buySideOrder.poll());
					 //System.out.println("Volume " + buySideOrder.peek().getVolume() + " deal with Price " + sellSideOrder.peek().getPrice());
				     //System.out.println(buySideOrder.poll() + " -----is removed"); 
					 return processedOrder;
				 }
				 else
					{
						//double limitPrice = 0;
						int sumBuyVolume = 0;
						
						while(buySideOrder.peek().getVolume() > sellSideOrder.peek().getVolume())
						{
								int bVolume = buySideOrder.peek().getVolume() - sellSideOrder.peek().getVolume();
								buySideOrder.peek().setVolume(bVolume);
								
								//limitPrice  = buySideOrder.peek().getPrice();
								sumBuyVolume += sellSideOrder.peek().getVolume();
								
								sellSideOrder.peek().setStatus(1);
								processedOrder.add(sellSideOrder.poll());
								//System.out.println(sellSideOrder.poll() + " -----is removed");
								//System.out.println("Volume " + sumBuyVolume + " has been processed, remaining " + buySideOrder.peek());
								 
							if(sellSideOrder.peek() != null)
							{
								if(buySideOrder.peek().getPrice() != sellSideOrder.peek().getPrice())
								{
									return processedOrder;
								}
								else
								{
									if(buySideOrder.peek().getVolume() < sellSideOrder.peek().getVolume())
									{
										int cVolume = sellSideOrder.peek().getVolume() - (buySideOrder.peek().getVolume());
										//System.out.println("-----removed volume " + buySideOrder.peek().getVolume() + " from " + sellSideOrder.peek());
										sellSideOrder.peek().setVolume(cVolume);
										//System.out.println("-----remain " + sellSideOrder.peek());
										//double allPrice = limitPrice;
										int allVolume = sumBuyVolume + (buySideOrder.peek().getVolume());
										//System.out.println("Volume " + allVolume + " deal with Price " + limitPrice);
										buySideOrder.peek().setStatus(1);
										buySideOrder.peek().setVolume(allVolume);
										processedOrder.add(buySideOrder.poll());
										//System.out.println(buySideOrder.poll() + " -----is removed");
										return processedOrder;
									}
									else if(buySideOrder.peek().getVolume() == sellSideOrder.peek().getVolume())
									{
										
										int allVolume = sumBuyVolume + (buySideOrder.peek().getVolume());
										buySideOrder.peek().setStatus(1);
										sellSideOrder.peek().setStatus(1);
										buySideOrder.peek().setVolume(allVolume);
										processedOrder.add(buySideOrder.poll());
										processedOrder.add(sellSideOrder.poll());
										
										return processedOrder;
									}
									else
										continue;
								}

							}
							else
							{
								return processedOrder;
							}
						}
					}
				 }
			  }
		  return processedOrder;
		}
	}
