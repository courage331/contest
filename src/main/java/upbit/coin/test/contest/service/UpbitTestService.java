package upbit.coin.test.contest.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upbit.coin.test.contest.vo.ResponseVO;
import upbit.coin.test.contest.vo.candle.MinuteCandleVO;
import upbit.coin.test.contest.vo.market.MarketCodeVO;
import upbit.coin.test.contest.vo.orderbook.OrderBookUnits;
import upbit.coin.test.contest.vo.orderbook.OrderBookVO;
import upbit.coin.test.contest.vo.ticker.TickerVO;


import java.net.InetAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UpbitTestService {

    @Autowired
    Gson gson;

    @Value("${upbit.address.market}")
    String marketurl;

    @Value("${upbit.address.minuteCandle}")
    String candleurl;

    @Value("${upbit.address.ticker}")
    String tickerurl;

    @Value("${upbit.address.orderbook}")
    String orderbookurl;

    @Value("${upbit.address.accounts}")
    String accountsurl;

    @Value("${upbit.key.AccessKey}")
    String accessKey;

    @Value("${upbit.key.SecretKey}")
    String secretKey;

    /**
     * Quotation API
     * 시세 종목 조회
     */
    public ResponseVO showMarketList() {
        ResponseVO responseVO = new ResponseVO();
        RestTemplate restTemplate = new RestTemplate();


        try {
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

            HttpHeaders headers = new HttpHeaders();

            headers.add("Accept", "application/json");

            final HttpEntity<String> entity = new HttpEntity(headers);

            ResponseEntity<String> response = restTemplate.exchange(marketurl, HttpMethod.GET, entity, String.class);
            JsonArray jsonObj = gson.fromJson(response.getBody(), JsonArray.class);


            int len = jsonObj.size();
            List<MarketCodeVO> marketCodeVOList = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                MarketCodeVO marketCodeVO = new MarketCodeVO();
                marketCodeVO.setMarket(jsonObj.get(i).getAsJsonObject().get("market").getAsString());
                marketCodeVO.setKorean_name(jsonObj.get(i).getAsJsonObject().get("korean_name").getAsString());
                marketCodeVO.setEnglish_name(jsonObj.get(i).getAsJsonObject().get("english_name").getAsString());
                marketCodeVOList.add(marketCodeVO);
            }

//            System.out.println(jsonObj.get(0).getAsJsonObject().get("english_name").getAsString());
//            System.out.println(jsonObj.get(0).getAsJsonObject().get("market").getAsString());
//            System.out.println(jsonObj.get(1).getAsJsonObject().get("english_name").getAsString());
//            System.out.println(jsonObj.get(1).getAsJsonObject().get("market").getAsString());

            responseVO.setResponseCode(200);
            responseVO.setResponseMessage("Success");
            responseVO.setData(marketCodeVOList);

            for (int i = 0; i < marketCodeVOList.size(); i++) {
                System.out.println(marketCodeVOList.get(i).getMarket());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseVO;
    }


    /**
     * Quotation API
     * 시세 캔들 조회 (분)
     */
    public ResponseVO showMinuteCandle(Map<String, Object> reqMap) {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String toDate = sdf.format(date);

        int unit = (int) (reqMap.get("unit"));
        String market = String.valueOf(reqMap.get("market"));
        String to = String.valueOf(reqMap.get("to")).equals("") ? toDate : String.valueOf(reqMap.get("to"));
        int count = (int) (reqMap.get("count"));

        ResponseVO responseVO = new ResponseVO();

        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

            HttpHeaders headers = new HttpHeaders();

            headers.add("Accept", "application/json");

            final HttpEntity<String> entity = new HttpEntity(headers);

            candleurl += "/" + unit + "?market=" + market + "&to=" + to + "&count=" + count;


            ResponseEntity<String> response = restTemplate.exchange(candleurl, HttpMethod.GET, entity, String.class);
            JsonArray jsonObj = gson.fromJson(response.getBody(), JsonArray.class);


            int len = jsonObj.size();
            List<MinuteCandleVO> minuteCandleList = new ArrayList();
            for (int i = 0; i < len; i++) {
                MinuteCandleVO minuteCandleVO = new MinuteCandleVO();
                minuteCandleVO.setMarket(jsonObj.get(i).getAsJsonObject().get("market").getAsString());
                minuteCandleVO.setCandle_date_time_utc(jsonObj.get(i).getAsJsonObject().get("candle_date_time_utc").getAsString());
                minuteCandleVO.setCandle_date_time_kst(jsonObj.get(i).getAsJsonObject().get("candle_date_time_kst").getAsString());
                minuteCandleVO.setOpening_price(jsonObj.get(i).getAsJsonObject().get("opening_price").getAsString());
                minuteCandleVO.setHigh_price(jsonObj.get(i).getAsJsonObject().get("high_price").getAsString());
                minuteCandleVO.setLow_price(jsonObj.get(i).getAsJsonObject().get("low_price").getAsString());
                minuteCandleVO.setTrade_price(jsonObj.get(i).getAsJsonObject().get("trade_price").getAsString());
                minuteCandleVO.setTimestamp(jsonObj.get(i).getAsJsonObject().get("timestamp").getAsString());
                minuteCandleVO.setCandle_acc_trade_price(jsonObj.get(i).getAsJsonObject().get("candle_acc_trade_price").getAsString());
                minuteCandleVO.setCandle_acc_trade_volume(jsonObj.get(i).getAsJsonObject().get("candle_acc_trade_volume").getAsString());
                minuteCandleVO.setUnit(jsonObj.get(i).getAsJsonObject().get("unit").getAsString());
                minuteCandleList.add(minuteCandleVO);
            }
            responseVO.setResponseCode(200);
            responseVO.setResponseMessage("Success");
            responseVO.setData(minuteCandleList);
            System.out.println(InetAddress.getLocalHost());
            //System.out.println(response.getHeaders());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return responseVO;
    }


    /**
     * Quotation API
     * 현재가 정보
     */
    public ResponseVO showTicker(String markets) {
        ResponseVO responseVO = new ResponseVO();

        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

            HttpHeaders headers = new HttpHeaders();

            headers.add("Accept", "application/json");

            final HttpEntity<String> entity = new HttpEntity(headers);

            tickerurl += "?markets=" + markets;

            ResponseEntity<String> response = restTemplate.exchange(tickerurl, HttpMethod.GET, entity, String.class);
            JsonArray jsonObj = gson.fromJson(response.getBody(), JsonArray.class);
            System.out.println(tickerurl);
            System.out.println(jsonObj.toString());

            int len = jsonObj.size();
            List<TickerVO> tickerVOList = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                TickerVO tickerVO = new TickerVO();
                tickerVO.setMarket(jsonObj.get(i).getAsJsonObject().get("market").getAsString());
                tickerVO.setTrade_date(jsonObj.get(i).getAsJsonObject().get("trade_date").getAsString());
                tickerVO.setTrade_time(jsonObj.get(i).getAsJsonObject().get("trade_time").getAsString());
                tickerVO.setTrade_date_kst(jsonObj.get(i).getAsJsonObject().get("trade_date_kst").getAsString());
                tickerVO.setTrade_time_kst(jsonObj.get(i).getAsJsonObject().get("trade_time_kst").getAsString());
                tickerVO.setOpening_price(jsonObj.get(i).getAsJsonObject().get("trade_timestamp").getAsString());
                tickerVO.setHigh_price(jsonObj.get(i).getAsJsonObject().get("opening_price").getAsString());
                tickerVO.setLow_price(jsonObj.get(i).getAsJsonObject().get("high_price").getAsString());
                tickerVO.setTrade_price(jsonObj.get(i).getAsJsonObject().get("low_price").getAsString());
                tickerVO.setPrev_closing_price(jsonObj.get(i).getAsJsonObject().get("prev_closing_price").getAsString());
                tickerVO.setChange(jsonObj.get(i).getAsJsonObject().get("change").getAsString());
                tickerVO.setChange_price(jsonObj.get(i).getAsJsonObject().get("change_price").getAsString());
                tickerVO.setChange_rate(jsonObj.get(i).getAsJsonObject().get("change_rate").getAsString());
                tickerVO.setSigned_change_rate(jsonObj.get(i).getAsJsonObject().get("signed_change_rate").getAsString());
                tickerVO.setSigned_change_price(jsonObj.get(i).getAsJsonObject().get("signed_change_price").getAsString());
                tickerVO.setTrade_volume(jsonObj.get(i).getAsJsonObject().get("trade_volume").getAsString());
                tickerVO.setAcc_trade_price(jsonObj.get(i).getAsJsonObject().get("acc_trade_price").getAsString());
                tickerVO.setAcc_trade_price_24h(jsonObj.get(i).getAsJsonObject().get("acc_trade_price_24h").getAsString());
                tickerVO.setAcc_trade_volume(jsonObj.get(i).getAsJsonObject().get("acc_trade_volume").getAsString());
                tickerVO.setAcc_trade_volume_24h(jsonObj.get(i).getAsJsonObject().get("acc_trade_volume_24h").getAsString());
                tickerVO.setHighest_52_week_date(jsonObj.get(i).getAsJsonObject().get("highest_52_week_date").getAsString());
                tickerVO.setHighest_52_week_price(jsonObj.get(i).getAsJsonObject().get("highest_52_week_price").getAsString());
                tickerVO.setLowest_52_week_date(jsonObj.get(i).getAsJsonObject().get("lowest_52_week_date").getAsString());
                tickerVO.setLowest_52_week_price(jsonObj.get(i).getAsJsonObject().get("lowest_52_week_price").getAsString());
                tickerVO.setTimestamp(jsonObj.get(i).getAsJsonObject().get("timestamp").getAsString());
                tickerVOList.add(tickerVO);
            }
            responseVO.setData(tickerVOList);

        } catch (Exception e) {

        }

        return responseVO;
    }


    /**
     * Quotation API
     * 시세 호가 정보
     */

    public ResponseVO showOrderBook(String markets) {

        ResponseVO responseVO = new ResponseVO();

        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

            HttpHeaders headers = new HttpHeaders();

            headers.add("Accept", "application/json");

            final HttpEntity<String> entity = new HttpEntity(headers);

            orderbookurl += "?markets=" + markets;

            ResponseEntity<String> response = restTemplate.exchange(orderbookurl, HttpMethod.GET, entity, String.class);

            JsonArray jsonObj = gson.fromJson(response.getBody(), JsonArray.class);

            int len = jsonObj.size();
            List<OrderBookVO> orderBookVOList = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                OrderBookVO orderBookVO = new OrderBookVO();
                orderBookVO.setMarket(jsonObj.get(i).getAsJsonObject().get("market").getAsString());
                orderBookVO.setTimestamp(jsonObj.get(i).getAsJsonObject().get("timestamp").getAsString());
                orderBookVO.setTotal_ask_size(jsonObj.get(i).getAsJsonObject().get("total_ask_size").getAsString());
                orderBookVO.setTotal_bid_size(jsonObj.get(i).getAsJsonObject().get("total_bid_size").getAsString());

                JsonArray jsonObj2 = gson.fromJson(jsonObj.get(i).getAsJsonObject().get("orderbook_units"), JsonArray.class);
                List<OrderBookUnits> orderBookUnitsList = new ArrayList<>();
                for (int j = 0; j < jsonObj2.size(); j++) {
                    OrderBookUnits orderBookUnits = new OrderBookUnits();
                    orderBookUnits.setAsk_price(jsonObj2.get(j).getAsJsonObject().get("ask_price").getAsInt());
                    orderBookUnits.setBid_price(jsonObj2.get(j).getAsJsonObject().get("bid_price").getAsInt());
                    orderBookUnits.setAsk_size(jsonObj2.get(j).getAsJsonObject().get("ask_size").getAsString());
                    orderBookUnits.setBid_size(jsonObj2.get(j).getAsJsonObject().get("bid_size").getAsString());

                    orderBookUnitsList.add(orderBookUnits);
                }
                orderBookVO.setOrderbook_units(orderBookUnitsList);

                orderBookVOList.add(orderBookVO);
            }
            responseVO.setResponseCode(200);
            responseVO.setResponseMessage("Success");
            responseVO.setData(orderBookVOList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseVO;
    }


}
